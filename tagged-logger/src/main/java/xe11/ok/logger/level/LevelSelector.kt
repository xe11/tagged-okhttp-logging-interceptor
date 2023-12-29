package xe11.ok.logger.level

import okhttp3.Request
import okhttp3.Response

internal interface LevelSelector {
    fun getBaseLevel(): Level
    fun getLevelFor(request: Request): Level
    fun getLevelFor(response: Response): Level
    fun getLevelFor(err: Exception): Level
}
