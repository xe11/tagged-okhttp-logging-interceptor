package xe11.ok.logger.tag

import okhttp3.Request
import java.util.concurrent.atomic.AtomicInteger

internal class RequestNumberTagProvider : TagProvider {

    private val counter = AtomicInteger()

    override fun getTag(request: Request, length: Short): String {
        val divider = power10(length)
        return (counter.incrementAndGet() % divider).toString()
    }

    private fun power10(exponentVal: Short): Int {
        var result = 1
        repeat(exponentVal.toInt()) { result *= 10 }
        return result
    }
}
