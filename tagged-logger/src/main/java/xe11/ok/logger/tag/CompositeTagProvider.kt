package xe11.ok.logger.tag

import okhttp3.Request

internal class CompositeTagProvider(
    private val tag: TagUtils.() -> String
) {

    private val urlTagProvider = UrlTagProvider()
    private val requestNumberTagProvider = RequestNumberTagProvider()

    fun getTag(request: Request): String {
        val tagUtils = TagUtilsImpl(request, urlTagProvider, requestNumberTagProvider)
        return tag(tagUtils)
    }
}
