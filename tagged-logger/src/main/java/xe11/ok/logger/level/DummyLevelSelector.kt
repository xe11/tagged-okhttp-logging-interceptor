package xe11.ok.logger.level

import okhttp3.Request
import okhttp3.Response

internal class DummyLevelSelector(private val baseLevel: Level) : LevelSelector {
    override fun getBaseLevel(): Level = baseLevel
    override fun getLevelFor(request: Request): Level = baseLevel
    override fun getLevelFor(response: Response): Level = baseLevel
    override fun getLevelFor(err: Exception): Level = baseLevel
}
