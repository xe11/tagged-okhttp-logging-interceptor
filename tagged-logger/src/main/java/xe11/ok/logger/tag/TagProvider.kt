package xe11.ok.logger.tag

import okhttp3.Request

internal interface TagProvider {

    fun getTag(request: Request, length: Short): String
}
