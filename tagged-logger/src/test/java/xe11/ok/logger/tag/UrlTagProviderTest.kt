package xe11.ok.logger.tag

import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import xe11.ok.logger.test.utils.toDynamicTest

private const val VALID_UUID = "26929514-237c-11ed-861d-0242ac120002"

internal class UrlTagProviderTest {

    private val sut = UrlTagProvider()

    @TestFactory
    fun `getTag should return expected url tag`(): List<DynamicTest> {
        data class TestData(
            val path: String,
            val length: Short,
            val expected: String,
        )

        val testData = listOf(
            TestData(path = "/", length = 10, expected = ""),
            TestData("", 10, ""),
            TestData("/Capitalized", 10, "Capitalize"),
            TestData("/test/end/point", 10, "TesEndPoi"),
            TestData("/long/test/end/point", 4, "EnPo"),
            TestData("/test/end/point/a/b/c/d", 4, "CD"),
            TestData("/test/end/point/a/b/c/d", 4, "CD"),
            TestData("/quite/long/test/end/point", 6, "TeEnPo"),
            TestData("/test/$VALID_UUID", 10, "TestId"),
            TestData("/test/2692951-237c-11ed-861d-0242ac120002", 10, "Test26929"),
            TestData("/test/26-929514237c-11ed861d-0242ac12-0002", 10, "Test26-92"),
            TestData("/test/123", 10, "TestId"),
            TestData("/test/id123", 10, "TestId"),
            // corner cases
            TestData("/", 0, ""),
            TestData("/", -1, ""),
            TestData("/Capitalized", 0, ""),
            TestData("/Capitalized", -1, ""),
            TestData("/%F0%9F%A4%94/", 10, "\uD83E\uDD14"),
            TestData("/\uD83E\uDD14/asdf", 10, "\uD83E\uDD14Asdf"),
            TestData("/\uD83E\uDD14asdf", 10, "\uD83E\uDD14asdf"),
            TestData("/\uD83E\uDD14asdfghj", 4, "\uD83E\uDD14as"),
        )

        return testData.toDynamicTest { (path, length, expected) ->
            val tag = sut.getTag(request(path), length)

            assertThat(tag).isEqualTo(expected)
        }
    }

    @Test
    fun `getTag should return Id as tag when segment matches to UUID`() {
        val path = "/test/26929514-237c-11ed-861d-0242ac120002"
        val length: Short = 10
        val expected = "TestId"

        val tag = sut.getTag(request(path), length)

        assertThat(tag).isEqualTo(expected)
    }

    @Test
    fun `getTag should return Id as tag when segment is numeric value`() {
        val path = "/test/1234567"
        val length: Short = 10
        val expected = "TestId"

        val tag = sut.getTag(request(path), length)

        assertThat(tag).isEqualTo(expected)
    }

    @Test
    fun `getTag should return Id as tag when segment is id+numeric value`() {
        val path = "/test/id1234567"
        val length: Short = 10
        val expected = "TestId"

        val tag = sut.getTag(request(path), length)

        assertThat(tag).isEqualTo(expected)
    }

    private fun request(path: String): Request =
        Request.Builder()
            .url("https://example.com$path")
            .build()
}
