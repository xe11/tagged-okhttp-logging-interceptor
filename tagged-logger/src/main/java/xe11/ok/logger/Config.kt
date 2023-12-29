package xe11.ok.logger

import xe11.ok.logger.Config.LoggingStrategy.Accumulate
import xe11.ok.logger.Config.LoggingStrategy.Accumulate.LogLevelScheme.HighlightedErrors
import xe11.ok.logger.level.Level
import xe11.ok.logger.printer.Printer
import xe11.ok.logger.printer.SystemoutPrinter
import xe11.ok.logger.tag.TagUtils

data class Config(
    val tag: TagUtils.() -> String = { "nwk: ${reqTag(16)} #${reqNum(3)}" },
    val loggingStrategy: LoggingStrategy = Accumulate(),
    val printer: Printer = SystemoutPrinter(),
) {
    sealed interface LoggingStrategy {

        data class PassThrough(
            val level: Level = Level.INFO,
        ) : LoggingStrategy

        data class Accumulate(
            val synchronizeLogging: Boolean = true,
            val logLevelScheme: LogLevelScheme = HighlightedErrors(),
        ) : LoggingStrategy {

            sealed interface LogLevelScheme {
                data class SingleLevel(val level: Level = Level.INFO) : LogLevelScheme
                data class HighlightedErrors(val baseLevel: Level = Level.INFO) : LogLevelScheme
            }
        }
    }

    companion object {
        val Default = Config()
    }
}
