package xe11.ok.logger.collector

import okhttp3.Request
import okhttp3.Response
import xe11.ok.logger.level.Level
import xe11.ok.logger.level.LevelSelector
import xe11.ok.logger.printer.Printer

internal class AccumulatingLogCollector(
    private val tag: String,
    private val levelSelector: LevelSelector,
    synchronizeLogging: Boolean = true,
    printer: Printer,
) : LogCollector {

    private val printerWrapper: PrinterWrapper = printerWrapper(synchronizeLogging, printer)
    private var level: Level = levelSelector.getBaseLevel()
    private val logs: MutableList<String> = mutableListOf()

    override fun onRequest(request: Request) {
        val requestStartMessage = "->> HIT ${request.method} ${request.url}"
        level = levelSelector.getLevelFor(request)
        printerWrapper.print(level, tag, requestStartMessage)
    }

    override fun onProceed(response: Response) {
        level = levelSelector.getLevelFor(response)
    }

    override fun onException(err: Exception) {
        level = levelSelector.getLevelFor(err)
    }

    override fun onDone() {
        flushLogs()
    }

    override fun log(message: String) {
        logs.add(message)
    }

    private fun flushLogs() {
        printerWrapper.print(level, tag, logs)
    }

    private fun printerWrapper(
        synchronizeLogging: Boolean,
        printer: Printer
    ): PrinterWrapper {
        return if (synchronizeLogging) {
            SyncedPrinterWrapper(printer)
        } else {
            FastPrinterWrapper(printer)
        }
    }
}

private interface PrinterWrapper {

    fun print(
        level: Level,
        tag: String,
        message: String,
    )

    fun print(
        level: Level,
        tag: String,
        messages: List<String>,
    )
}

private class FastPrinterWrapper(private val printer: Printer) : PrinterWrapper {

    override fun print(level: Level, tag: String, message: String) {
        printer.print(level, tag, message)
    }

    override fun print(level: Level, tag: String, messages: List<String>) {
        messages.forEach { message ->
            printer.print(level, tag, message)
        }
    }
}

private class SyncedPrinterWrapper(private val printer: Printer) : PrinterWrapper {

    private val simpleWrapper = FastPrinterWrapper(printer)

    override fun print(level: Level, tag: String, message: String) {
        synchronized(printer) {
            simpleWrapper.print(level, tag, message)
        }
    }

    override fun print(level: Level, tag: String, messages: List<String>) {
        synchronized(printer) {
            simpleWrapper.print(level, tag, messages)
        }
    }
}
