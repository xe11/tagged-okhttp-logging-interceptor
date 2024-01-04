package xe11.ok.logger.tag

import okhttp3.Request
import java.util.Locale
import java.util.regex.Pattern

private val UUID_REGEX: Pattern =
    Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
private val NUMERIC_REGEX: Pattern = Pattern.compile("^\\d{2,}$")
private val ID_NUMERIC_REGEX: Pattern = Pattern.compile("^id\\d{2,}$")

private val PATH_SEGMENTS_REPLACEMENTS: List<Pattern> = listOf(
    UUID_REGEX,
    NUMERIC_REGEX,
    ID_NUMERIC_REGEX,
)

internal class UrlTagProvider : TagProvider {

    override fun getTag(request: Request, length: Short): String {
        if (length < 1) return ""

        val pathSegments = request.url.pathSegments
        val pathSegmentsShortened = pathSegmentsShortened(pathSegments, length)

        return pathSegmentsShortened.joinToString(separator = "")
    }

    private fun pathSegmentsShortened(pathSegments: List<String>, maxSize: Short): List<String> {
        val segmentsSize = pathSegments.size
        if (segmentsSize == 1 && pathSegments[0] == "") {
            return emptyList()
        }

        val segments = prepareSegments(pathSegments)

        return createShortenedTag(segments, maxSize)
    }

    private fun prepareSegments(pathSegments: List<String>): List<String> {
        return pathSegments.map { segment ->
            val isId = PATH_SEGMENTS_REPLACEMENTS.any { it.matcher(segment).matches() }
            if (isId) "Id" else segment
        }
    }

    private fun createShortenedTag(
        pathSegments: List<String>,
        maxSize: Short,
    ): List<String> {
        val segmentsSize = pathSegments.size
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
