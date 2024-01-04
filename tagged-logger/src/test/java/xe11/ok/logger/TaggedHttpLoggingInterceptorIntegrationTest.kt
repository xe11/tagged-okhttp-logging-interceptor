package xe11.ok.logger

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xe11.ok.logger.Config.LoggingStrategy.Accumulate.LogLevelScheme.HighlightedErrors
import xe11.ok.logger.level.Level
import xe11.ok.logger.level.OkHttpLogLevel
import xe11.ok.logger.printer.ChunkingPrinter
import xe11.ok.logger.printer.Printer
import java.io.InterruptedIOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

internal class TaggedHttpLoggingInterceptorIntegrationTest {

    private val server = MockWebServer()

    private val testPrinter = TestPrinter()

    @BeforeEach
    fun setUp() {
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `request should be logged as INFO when request succeed`() {
        val url = server.url("/test/end/point")
        val port = server.port
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "okhttp/test")
            .build()
        val response = MockResponse()
            .setResponseCode(200)
            .setHeader("Test-Header", 42)
            .setBody(
                """
                {
                    "success": "response",
                    "body": ["message"]
                }
                """.trimIndent()
            )

        server.enqueue(response)


        val client = buildClient()
        client.newCall(request).execute()

        testPrinter.assertLines(
            """
            INFO  nwk: TesEndPoi #1  ->> HIT GET http://localhost:$port/test/end/point
            INFO  nwk: TesEndPoi #1  --> GET http://localhost:$port/test/end/point http/1.1
            INFO  nwk: TesEndPoi #1  User-Agent: okhttp/test
            INFO  nwk: TesEndPoi #1  Host: localhost:$port
            INFO  nwk: TesEndPoi #1  Connection: Keep-Alive
            INFO  nwk: TesEndPoi #1  Accept-Encoding: gzip
            INFO  nwk: TesEndPoi #1  --> END GET
            INFO  nwk: TesEndPoi #1  <-- 200 OK http://localhost:$port/test/end/point
            INFO  nwk: TesEndPoi #1  Test-Header: 42
            INFO  nwk: TesEndPoi #1  Content-Length: 54
            INFO  nwk: TesEndPoi #1  {
            INFO  nwk: TesEndPoi #1      "success": "response",
            INFO  nwk: TesEndPoi #1      "body": ["message"]
            INFO  nwk: TesEndPoi #1  }
            INFO  nwk: TesEndPoi #1  <-- END HTTP (54-byte body)
            """
        )
    }

    @Test
    fun `request should be logged as ERROR and re-throw exception when request timeout occurred`() {
        val url = server.url("/test")
        val port = server.port
        val request = Request.Builder()
            .post(
                """
                {
                    "post": "request",
                    "body": 42
                }
                """.trimIndent().toRequestBody()
            )
            .url(url)
            .header("User-Agent", "okhttp/test")
            .build()


        val client = buildClient()

        assertThatThrownBy {
            client.newCall(request).execute()
        }.isInstanceOf(InterruptedIOException::class.java)

        testPrinter.assertLines(
            """
            INFO  nwk: Test #1  ->> HIT POST http://localhost:$port/test
            ERROR  nwk: Test #1  --> POST http://localhost:$port/test http/1.1
            ERROR  nwk: Test #1  User-Agent: okhttp/test
            ERROR  nwk: Test #1  Content-Length: 41
            ERROR  nwk: Test #1  Host: localhost:$port
            ERROR  nwk: Test #1  Connection: Keep-Alive
            ERROR  nwk: Test #1  Accept-Encoding: gzip
            ERROR  nwk: Test #1  {
            ERROR  nwk: Test #1      "post": "request",
            ERROR  nwk: Test #1      "body": 42
            ERROR  nwk: Test #1  }
            ERROR  nwk: Test #1  --> END POST (41-byte body)
            ERROR  nwk: Test #1  <-- HTTP FAILED: java.net.SocketException: Socket closed
            """
        )
    }

    @Test
    fun `request should be logged as WARN when request fails with 4xx error`() {
        val url = server.url("/test/a/b")
        val port = server.port
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "okhttp/test")
            .build()
        val response = MockResponse()
            .setResponseCode(400)
            .setHeader("Test-Header", 42)
            .setBody(
                """
                Error 
                response
                body
                """.trimIndent()
            )

        server.enqueue(response)


        val client = buildClient()
        client.newCall(request).execute()

        testPrinter.assertLines(
            """
            INFO  nwk: TesAB #1  ->> HIT GET http://localhost:$port/test/a/b
            WARN  nwk: TesAB #1  --> GET http://localhost:$port/test/a/b http/1.1
            WARN  nwk: TesAB #1  User-Agent: okhttp/test
            WARN  nwk: TesAB #1  Host: localhost:$port
            WARN  nwk: TesAB #1  Connection: Keep-Alive
            WARN  nwk: TesAB #1  Accept-Encoding: gzip
            WARN  nwk: TesAB #1  --> END GET
            WARN  nwk: TesAB #1  <-- 400 Client Error http://localhost:$port/test/a/b
            WARN  nwk: TesAB #1  Test-Header: 42
            WARN  nwk: TesAB #1  Content-Length: 20
            WARN  nwk: TesAB #1  Error 
            WARN  nwk: TesAB #1  response
            WARN  nwk: TesAB #1  body
            WARN  nwk: TesAB #1  <-- END HTTP (20-byte body)
            """
        )
    }

    @Test
    fun `request should be logged as ERROR when request fails with 5xx error`() {
        val url = server.url("/test/a/b")
        val port = server.port
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "okhttp/test")
            .build()
        val response = MockResponse()
            .setResponseCode(503)
            .setHeader("Test-Header", 42)
            .setBody(
                """
                Error 
                response
                body
                """.trimIndent()
            )

        server.enqueue(response)


        val client = buildClient()
        client.newCall(request).execute()

        testPrinter.assertLines(
            """
            INFO  nwk: TesAB #1  ->> HIT GET http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #1  --> GET http://localhost:$port/test/a/b http/1.1
            ERROR  nwk: TesAB #1  User-Agent: okhttp/test
            ERROR  nwk: TesAB #1  Host: localhost:$port
            ERROR  nwk: TesAB #1  Connection: Keep-Alive
            ERROR  nwk: TesAB #1  Accept-Encoding: gzip
            ERROR  nwk: TesAB #1  --> END GET
            ERROR  nwk: TesAB #1  <-- 503 Server Error http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #1  Test-Header: 42
            ERROR  nwk: TesAB #1  Content-Length: 20
            ERROR  nwk: TesAB #1  Error 
            ERROR  nwk: TesAB #1  response
            ERROR  nwk: TesAB #1  body
            ERROR  nwk: TesAB #1  <-- END HTTP (20-byte body)
            """
        )
    }

    @Test
    fun `requests should be logged with their sequence number`() {
        val url = server.url("/test/a/b")
        val port = server.port
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "okhttp/test")
            .build()
        val response = MockResponse()
            .setResponseCode(503)
            .setHeader("Test-Header", 42)
            .setBody(
                """
                Error 
                response
                body
                """.trimIndent()
            )


        val client = buildClient()
        repeat(3) {
            server.enqueue(response)
            client.newCall(request).execute()
        }

        testPrinter.assertLines(
            """
            INFO  nwk: TesAB #1  ->> HIT GET http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #1  --> GET http://localhost:$port/test/a/b http/1.1
            ERROR  nwk: TesAB #1  User-Agent: okhttp/test
            ERROR  nwk: TesAB #1  Host: localhost:$port
            ERROR  nwk: TesAB #1  Connection: Keep-Alive
            ERROR  nwk: TesAB #1  Accept-Encoding: gzip
            ERROR  nwk: TesAB #1  --> END GET
            ERROR  nwk: TesAB #1  <-- 503 Server Error http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #1  Test-Header: 42
            ERROR  nwk: TesAB #1  Content-Length: 20
            ERROR  nwk: TesAB #1  Error 
            ERROR  nwk: TesAB #1  response
            ERROR  nwk: TesAB #1  body
            ERROR  nwk: TesAB #1  <-- END HTTP (20-byte body)
            INFO  nwk: TesAB #2  ->> HIT GET http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #2  --> GET http://localhost:$port/test/a/b http/1.1
            ERROR  nwk: TesAB #2  User-Agent: okhttp/test
            ERROR  nwk: TesAB #2  Host: localhost:$port
            ERROR  nwk: TesAB #2  Connection: Keep-Alive
            ERROR  nwk: TesAB #2  Accept-Encoding: gzip
            ERROR  nwk: TesAB #2  --> END GET
            ERROR  nwk: TesAB #2  <-- 503 Server Error http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #2  Test-Header: 42
            ERROR  nwk: TesAB #2  Content-Length: 20
            ERROR  nwk: TesAB #2  Error 
            ERROR  nwk: TesAB #2  response
            ERROR  nwk: TesAB #2  body
            ERROR  nwk: TesAB #2  <-- END HTTP (20-byte body)
            INFO  nwk: TesAB #3  ->> HIT GET http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #3  --> GET http://localhost:$port/test/a/b http/1.1
            ERROR  nwk: TesAB #3  User-Agent: okhttp/test
            ERROR  nwk: TesAB #3  Host: localhost:$port
            ERROR  nwk: TesAB #3  Connection: Keep-Alive
            ERROR  nwk: TesAB #3  Accept-Encoding: gzip
            ERROR  nwk: TesAB #3  --> END GET
            ERROR  nwk: TesAB #3  <-- 503 Server Error http://localhost:$port/test/a/b
            ERROR  nwk: TesAB #3  Test-Header: 42
            ERROR  nwk: TesAB #3  Content-Length: 20
            ERROR  nwk: TesAB #3  Error 
            ERROR  nwk: TesAB #3  response
            ERROR  nwk: TesAB #3  body
            ERROR  nwk: TesAB #3  <-- END HTTP (20-byte body)
            """
        )
    }

    @Test
    fun `request with PassThrough strategy should be logged as INFO when request succeed`() {
        val url = server.url("/test/end/point")
        val port = server.port
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "okhttp/test")
            .build()
        val response = MockResponse()
            .setResponseCode(200)
            .setHeader("Test-Header", 42)
            .setBody(
                """
                {
                    "success": "response",
                    "body": ["message"]
                }
                """.trimIndent()
            )

        server.enqueue(response)


        val client = buildClient(
            Config(
                tag = {
                    "nwk: ${reqTag(8)} #${reqNum(3)}"
                },
                loggingStrategy = Config.LoggingStrategy.PassThrough(
                    level = Level.INFO
                ),
                printer = ChunkingPrinter(testPrinter),
            )
        )
        client.newCall(request).execute()

        testPrinter.assertLines(
            """
            INFO  nwk: TeEnPo #1  --> GET http://localhost:$port/test/end/point http/1.1
            INFO  nwk: TeEnPo #1  User-Agent: okhttp/test
            INFO  nwk: TeEnPo #1  Host: localhost:$port
            INFO  nwk: TeEnPo #1  Connection: Keep-Alive
            INFO  nwk: TeEnPo #1  Accept-Encoding: gzip
            INFO  nwk: TeEnPo #1  --> END GET
            INFO  nwk: TeEnPo #1  <-- 200 OK http://localhost:$port/test/end/point
            INFO  nwk: TeEnPo #1  Test-Header: 42
            INFO  nwk: TeEnPo #1  Content-Length: 54
            INFO  nwk: TeEnPo #1  {
            INFO  nwk: TeEnPo #1      "success": "response",
            INFO  nwk: TeEnPo #1      "body": ["message"]
            INFO  nwk: TeEnPo #1  }
            INFO  nwk: TeEnPo #1  <-- END HTTP (54-byte body)
            """
        )
    }

    private fun buildClient(config: Config? = null): OkHttpClient {
        val cfg = config ?: defaultTestConfig()

        val interceptor = TaggedHttpLoggingInterceptor(
            DefaultLogCollectorFactory(cfg)
        ).apply { level = OkHttpLogLevel.BODY }

        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(interceptor)
            .callTimeout(300.milliseconds.toJavaDuration())
            .build()

        return client
    }

    private fun defaultTestConfig(): Config =
        Config(
            tag = {
                "nwk: ${reqTag(10)} #${reqNum(3)}"
            },
            loggingStrategy = Config.LoggingStrategy.Accumulate(
                synchronizeLogging = true,
                logLevelScheme = HighlightedErrors(),
            ),
            printer = ChunkingPrinter(testPrinter),
        )
}

private fun TestPrinter.assertLines(expectedMultiline: String) {
    val expected = expectedMultiline.trimIndent().split('\n')

    logItems.forEachIndexed { index, logItem ->
        val message = logItem.run { "$level  $tag  $message" }.removeRequestDuration()
        assertThat(message).isEqualTo(expected[index])
    }
}

private fun String.removeRequestDuration(): String {
    return if (this.endsWith("ms)")) {
        this.substring(0, this.lastIndexOf(" ("))
    } else {
        this
    }
}

internal class TestPrinter : Printer {

    val logItems = mutableListOf<LogItem>()

    override fun print(level: Level, tag: String, message: String) {
        println("$level  $tag  $message")
        logItems.add(LogItem(level, tag, message))
    }

    data class LogItem(
        val level: Level,
        val tag: String,
        val message: String,
    )
}
