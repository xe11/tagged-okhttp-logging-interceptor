package xe11.ok.logger.level

import okhttp3.Request
import okhttp3.Response

internal class ResponseCodeLevelSelector(private val baseLevel: Level) : LevelSelector {

    override fun getBaseLevel(): Level = baseLevel

    override fun getLevelFor(request: Request): Level = baseLevel

    override fun getLevelFor(response: Response): Level {
        return when (response.code) {
            in 400..499 -> Level.WARN
            in 500..599 -> Level.ERROR
            else -> baseLevel
        }
    }

    override fun getLevelFor(err: Exception): Level = Level.ERROR
}

