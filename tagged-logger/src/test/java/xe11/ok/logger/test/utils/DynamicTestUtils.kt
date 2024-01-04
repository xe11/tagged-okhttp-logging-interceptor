package xe11.ok.logger.test.utils

import org.junit.jupiter.api.DynamicTest

fun <TData : Any> Iterable<TData>.toDynamicTest(assertBlock: (TData) -> Unit): List<DynamicTest> {
    return this.map { data: TData ->
        dynamicTest(data, assertBlock)
    }
}

fun <TData : Any> dynamicTest(data: TData, assertBlock: (TData) -> Unit): DynamicTest {
    return DynamicTest.dynamicTest(data.toString()) { assertBlock(data) }
}
