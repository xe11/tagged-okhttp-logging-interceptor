package xe11.ok.logging.example

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import xe11.ok.logger.Config.LoggingStrategy
import xe11.ok.logger.taggedHttpLoggingInterceptor
import java.util.concurrent.CountDownLatch
import okhttp3.logging.HttpLoggingInterceptor.Level as OkHttpLogLevel

private val TAG: String = "${TaggedLoggerShowcase::class.simpleName}"
private const val baseUrl = "https://postman-echo.com"

internal class TaggedLoggerShowcase {

    private var loggingStrategy: LoggingStrategy? = null

    private lateinit var okHttpClient: OkHttpClient

    fun launchParallelRequestsShowcase() {
        buildList<Request> {
            repeat(30) {
                get(path = "/delay/2")
            }
        }
            .executeAll(okHttpClient)
    }

    fun launchTagsAndHighlightingShowcase() {
        buildList<Request> {
            get(path = "/status/503")
            get(path = "/status/400")
            get(path = "/status/302")
            post(path = "/asdf/qwerty")
            post(path = "/some/very/very/very/long/path/that/wont/fit/tag/length")
            post(path = "/a/b/c/d/e/f/g/h/i/g/k/l/m/n/o/p/q")
            post(path = "/abcdefghigklmnopq")
        }
            .executeAll(okHttpClient)
    }

    fun initHttpClient(loggingStrategy: LoggingStrategy?) {
        if (loggingStrategy != null && this.loggingStrategy == loggingStrategy) {
            return
        }
        this.loggingStrategy = loggingStrategy

        val loggingInterceptor: Interceptor = if (loggingStrategy != null) {
            taggedHttpLoggingInterceptor(
                level = OkHttpLogLevel.BODY,
            ) { defaultConfig ->
                defaultConfig.copy(loggingStrategy = loggingStrategy)
                    .also { Log.d("nwk: $TAG", "okHttpClient: config: $it") }
            }
        } else {
            HttpLoggingInterceptor().apply { this.level = HttpLoggingInterceptor.Level.BODY }
        }

        okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }
}

private fun MutableList<Request>.get(path: String) {
    add(
        Request.Builder()
            .url("$baseUrl$path")
            .get()
            .build()
    )
}

private fun MutableList<Request>.post(path: String) {
    add(
        Request.Builder()
            .url("$baseUrl$path")
            .post(createBody(lines = 3).toRequestBody())
            .build()
    )
}

private fun createBody(lines: Int): String {
    return StringBuilder()
        .apply {
            repeat(lines) { index ->
                appendLine("request body line $index")
            }
        }
        .toString()
}

private fun List<Request>.executeAll(client: OkHttpClient) {
    val latch = CountDownLatch(size)
    this.forEach { request ->
        Thread {
            val call = client.newCall(request)
            latch.countDown()
            try {
                call.execute()
            } catch (e: Exception) {
                Log.e(TAG, "executeAll", e)
            }
        }.start()
    }
}
