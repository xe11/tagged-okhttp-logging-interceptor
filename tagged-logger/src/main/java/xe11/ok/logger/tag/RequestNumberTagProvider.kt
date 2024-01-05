package xe11.ok.logger.tag

import okhttp3.Request
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

internal class RequestNumberTagProvider : TagProvider {

    private val counter = AtomicInteger()

    override fun getTag(request: Request, length: Short): String {
        val divider = power10(length.toInt())
        return (counter.incrementAndGet() % divider).toString()
    }

    private fun power10(exponent: Int): Int {
        var result = 1
        repeat(min(9, exponent)) { result *= 10 }
        return result
    }

    companion object {

        private var _globalCounter = RequestNumberTagProvider()
        val globalCounter: RequestNumberTagProvider
            get() = _globalCounter

        internal fun resetGlobalCounter() {
            _globalCounter = RequestNumberTagProvider()
        }
    }
}
