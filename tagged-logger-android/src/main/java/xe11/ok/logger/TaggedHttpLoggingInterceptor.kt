package xe11.ok.logger

import xe11.ok.logger.printer.AndroidPrinter
import okhttp3.logging.HttpLoggingInterceptor.Level as OkHttpLogLevel

val DefaultAndroidConfig: Config get() = Config.Default.copy(printer = AndroidPrinter())

fun taggedHttpLoggingInterceptor(
    level: OkHttpLogLevel = OkHttpLogLevel.BODY,
    config: (defaultConfig: Config) -> Config,
): TaggedHttpLoggingInterceptor {
    return taggedHttpLoggingInterceptor(
        level = level,
        config = config(DefaultAndroidConfig),
    )
}

fun taggedHttpLoggingInterceptor(
    level: OkHttpLogLevel = OkHttpLogLevel.BODY,
    config: Config = DefaultAndroidConfig,
): TaggedHttpLoggingInterceptor {
    return TaggedHttpLoggingInterceptor(
        createLogCollector = DefaultLogCollectorFactory(config = config),
    )
        .apply { this.level = level }
}
