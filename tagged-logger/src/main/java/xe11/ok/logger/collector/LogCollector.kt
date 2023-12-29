package xe11.ok.logger.collector

import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

interface LogCollector : HttpLoggingInterceptor.Logger {
    fun onRequest(request: Request)
    fun onProceed(response: Response)
    fun onDone()
    fun onException(err: Exception)
}
