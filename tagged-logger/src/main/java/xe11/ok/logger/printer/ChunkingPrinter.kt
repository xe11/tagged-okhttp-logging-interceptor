package xe11.ok.logger.printer

import xe11.ok.logger.level.Level

private const val MAX_LOG_LENGTH = 4000

class ChunkingPrinter(
    private val logger: Printer,
) : Printer {

    override fun print(level: Level, tag: String, message: String) {
        // Split by line, then ensure each line can fit into Log's maximum length.
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = minOf(newline, i + MAX_LOG_LENGTH)
                val chunkedMessage = message.substring(i, end)
                logger.print(level, tag, chunkedMessage)
                i = end
            } while (i < newline)
            i++
        }
    }
}
