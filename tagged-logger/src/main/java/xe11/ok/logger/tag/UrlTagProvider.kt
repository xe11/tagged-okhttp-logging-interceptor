package xe11.ok.logger.tag

import okhttp3.Request
import java.util.Locale

internal class UrlTagProvider : TagProvider {

    override fun getTag(request: Request, length: Short): String {
        val pathSegments = request.url.pathSegments
        val pathSegmentsShortened = pathSegmentsShortened(pathSegments, length)

        return pathSegmentsShortened.joinToString(separator = "")
    }

    private fun pathSegmentsShortened(pathSegments: List<String>, maxSize: Short): List<String> {
        val segmentsSize = pathSegments.size
        if (segmentsSize == 1 && pathSegments[0] == "") {
            return emptyList()
        }

        var includedSegmentsCount = segmentsSize
        var chunkSize = maxSize / segmentsSize
        while (chunkSize < 2) {
            --includedSegmentsCount
            chunkSize = maxSize / includedSegmentsCount
        }

        val pathSegmentsForTag = pathSegments.subList(pathSegments.size - includedSegmentsCount, pathSegments.size)
        return pathSegmentsForTag
            .map { segment ->
                tagSegment(segment, chunkSize)
            }
    }

    private fun tagSegment(segment: String, length: Int): String =
        segment
            .substring(0, Integer.min(segment.length, length))
            .lowercase()
            .capitalize()

    private fun String.capitalize(): String =
        replaceFirstChar { firstChar -> firstChar.titlecase(Locale.getDefault()) }
}
