package xe11.ok.logger

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import xe11.ok.logger.collector.LogCollector
import xe11.ok.logger.level.OkHttpLogLevel

class TaggedHttpLoggingInterceptor(
    private val createLogCollector: LogCollectorFactory,
) : Interceptor {

    @Volatile
    var level = OkHttpLogLevel.NONE

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if (this.level == OkHttpLogLevel.NONE) {
            return chain.proceed(request)
        }

        val logCollector: LogCollector = createLogCollector(request)

        return intercept(chain, request, logCollector)
    }

    private fun intercept(
        chain: Interceptor.Chain,
        request: Request,
        logCollector: LogCollector,
    ): Response {
        val chainDecorator = object : Interceptor.Chain by chain {

            override fun request(): Request {
                return request
                    .also(logCollector::onRequest)
            }

            override fun proceed(request: Request): Response {
                return try {
                    chain.proceed(request)
                        .also(logCollector::onProceed)
                } catch (exception: Exception) {
                    logCollector.onException(exception)
                    throw exception
                }
            }
        }

        return try {
            createLoggingInterceptor(logCollector)
                .intercept(chainDecorator)
        } finally {
            logCollector.onDone()
        }
    }

    private fun createLoggingInterceptor(logCollector: LogCollector): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(logCollector)
            .also { it.level = this.level }
    }
}
