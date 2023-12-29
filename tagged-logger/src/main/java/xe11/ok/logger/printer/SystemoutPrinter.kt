package xe11.ok.logger.printer

import xe11.ok.logger.level.Level

class SystemoutPrinter : Printer {

    override fun print(level: Level, tag: String, message: String) {
        println("$tag $message")
    }
}
