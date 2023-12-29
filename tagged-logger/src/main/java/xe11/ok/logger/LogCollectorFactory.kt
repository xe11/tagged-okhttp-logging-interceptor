package xe11.ok.logger

import okhttp3.Request
import xe11.ok.logger.Config.LoggingStrategy.Accumulate
import xe11.ok.logger.Config.LoggingStrategy.Accumulate.LogLevelScheme.HighlightedErrors
import xe11.ok.logger.Config.LoggingStrategy.Accumulate.LogLevelScheme.SingleLevel
import xe11.ok.logger.Config.LoggingStrategy.PassThrough
import xe11.ok.logger.collector.AccumulatingLogCollector
import xe11.ok.logger.collector.LogCollector
import xe11.ok.logger.collector.PassThroughLogCollector
import xe11.ok.logger.level.DummyLevelSelector
import xe11.ok.logger.level.ResponseCodeLevelSelector
import xe11.ok.logger.printer.Printer
import xe11.ok.logger.tag.CompositeTagProvider

fun interface LogCollectorFactory {

    operator fun invoke(request: Request): LogCollector
}

class DefaultLogCollectorFactory(
    config: Config,
) : LogCollectorFactory {

    private val tagProvider: CompositeTagProvider = CompositeTagProvider(config.tag)
    private val createLogCollector: ((tag: String) -> LogCollector) = initLogCollectorFactory(config)

    override fun invoke(request: Request): LogCollector {
        return createLogCollector(tagProvider.getTag(request))
    }

    private fun initLogCollectorFactory(config: Config): (tag: String) -> LogCollector {
        val strategy = config.loggingStrategy
        val printer = config.printer

        return when (strategy) {
            is Accumulate -> { tag: String -> createAccumulating(tag, strategy, printer) }
            is PassThrough -> { tag: String -> createPassThrough(tag, strategy, printer) }
        }
    }

    private fun createAccumulating(
        tag: String,
        config: Accumulate,
        printer: Printer,
    ): AccumulatingLogCollector {
        val logLevelScheme = config.logLevelScheme
        val levelSelector = when (logLevelScheme) {
            is HighlightedErrors -> ResponseCodeLevelSelector(logLevelScheme.baseLevel)
            is SingleLevel -> DummyLevelSelector(logLevelScheme.level)
        }

        return AccumulatingLogCollector(
            tag = tag,
            levelSelector = levelSelector,
            synchronizeLogging = config.synchronizeLogging,
            printer = printer,
        )
    }

    private fun createPassThrough(
        tag: String,
        config: PassThrough,
        printer: Printer,
    ): PassThroughLogCollector {
        return PassThroughLogCollector(
            tag = tag,
            levelSelector = DummyLevelSelector(config.level),
            printer = printer,
        )
    }
}
