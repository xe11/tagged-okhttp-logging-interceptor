package xe11.ok.logger.tag

import okhttp3.Request

internal class TagUtilsImpl(
    private val request: Request,
    private val urlTagProvider: TagProvider,
    private val requestNumberTagProvider: TagProvider,
) : TagUtils {

    override fun reqTag(length: Short): String {
        return urlTagProvider.getTag(request, length)
    }

    override fun reqNum(length: Short): String {
        return requestNumberTagProvider.getTag(request, length)
    }
}
