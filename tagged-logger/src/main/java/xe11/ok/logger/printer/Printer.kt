package xe11.ok.logger.printer

import xe11.ok.logger.level.Level

interface Printer {

    fun print(
        level: Level,
        tag: String,
        message: String,
    )
}
