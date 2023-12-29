package xe11.ok.logger.collector

import okhttp3.Request
import okhttp3.Response
import xe11.ok.logger.level.Level
import xe11.ok.logger.level.LevelSelector
import xe11.ok.logger.printer.Printer

internal class PassThroughLogCollector(
    private val tag: String,
    private val levelSelector: LevelSelector,
    private val printer: Printer,
) : LogCollector {

    private var level: Level = levelSelector.getBaseLevel()

    override fun onRequest(request: Request) {
        level = levelSelector.getLevelFor(request)
    }

    override fun onProceed(response: Response) = Unit
    override fun onDone() = Unit
    override fun onException(err: Exception) = Unit

    override fun log(message: String) {
        printer.print(level, tag, message)
    }
}
