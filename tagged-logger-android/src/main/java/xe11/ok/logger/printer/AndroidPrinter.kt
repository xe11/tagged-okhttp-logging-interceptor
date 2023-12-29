package xe11.ok.logger.printer

import android.util.Log
import xe11.ok.logger.level.Level

class AndroidPrinter(
    printer: Printer = LogCatPrinter()
) : Printer {

    private val chunkingLogger: ChunkingPrinter = ChunkingPrinter(printer)

    override fun print(level: Level, tag: String, message: String) {
        val logLevel = Log.INFO
        if (Log.isLoggable(tag, logLevel)) {
            chunkingLogger.print(level, tag, message)
        }
    }
}

private class LogCatPrinter : Printer {

    private val logLevelMapper = LevelMapper()

    override fun print(level: Level, tag: String, message: String) {
        val logLevel = logLevelMapper.map(level)
        Log.println(logLevel, tag, message)
    }
}

private class LevelMapper {

    fun map(level: Level): Int {
        return when (level) {
            Level.VERBOSE -> Log.VERBOSE
            Level.DEBUG -> Log.DEBUG
            Level.INFO -> Log.INFO
            Level.WARN -> Log.WARN
            Level.ERROR -> Log.ERROR
        }
    }
}
