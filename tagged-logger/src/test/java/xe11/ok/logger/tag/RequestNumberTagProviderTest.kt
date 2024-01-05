package xe11.ok.logger.tag

import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import xe11.ok.logger.test.utils.toDynamicTest

class RequestNumberTagProviderTest {

    private val request: Request =
        Request.Builder()
            .url("https://example.com")
            .build()

    private val sut = RequestNumberTagProvider()

    @Test
    fun `getTag should return incrementing request number tag`() {
        val length: Short = 1

        for (index in 1..9) {
            val expected = index.toString()

            val tag = sut.getTag(request, length)

            assertThat(tag).isEqualTo(expected)
        }
    }

    @Test
    fun `getTag should return wrap around 0 tag when request number length exceeds tag length`() {
        val length: Short = 2
        val expected102 = "2"

        for (index in 1..101) {
            val expected = (index % 100).toString()
            val tag = sut.getTag(request, length)

            assertThat(tag).isEqualTo(expected)
        }

        val tag = sut.getTag(request, length)

        assertThat(tag).isEqualTo(expected102)
    }

    @TestFactory
    fun `getTag should return '0' tag for 0 and negative tag length`(): List<DynamicTest> {
        val expected = "0"

        val testData = listOf(
            0,
            -1,
            -42,
            Short.MIN_VALUE.toInt(),
        ).map(Int::toShort)

        return testData.toDynamicTest { length ->
            val tag1 = sut.getTag(request, length)

            assertThat(tag1).isEqualTo("0")

            repeat(42) { sut.getTag(request, length) }
            val tag101 = sut.getTag(request, length)
            assertThat(tag101).isEqualTo(expected)
        }
    }
}
