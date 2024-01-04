Tagged OkHttp Logging Interceptor
===================
[![codecov](https://codecov.io/gh/xe11/tagged-okhttp-logging-interceptor/graph/badge.svg?token=YOSWTZ35LP)](https://codecov.io/gh/xe11/tagged-okhttp-logging-interceptor)

An [OkHttp][link-okhttp] interceptor that prettifies OkHttp logs and makes them more readable, informative and
filterable by adding a unique tag to each request.

It is especially useful in the new Android LogCat:

*Default OkHttp logger*
<img width="1199" alt="okhttp-logging-interceptor" src="https://github.com/xe11/tagged-okhttp-logging-interceptor/assets/670736/ad7fcbf0-7f7b-4257-8198-6eabf6505636">

*Tagged logger*
<img width="1205" alt="tagged-okhttp-logging-interceptor-1" src="https://github.com/xe11/tagged-okhttp-logging-interceptor/assets/670736/79b2d348-0824-4a32-b4a3-b71f409d23a9">
<img width="1189" alt="tagged-okhttp-logging-interceptor-2" src="https://github.com/xe11/tagged-okhttp-logging-interceptor/assets/670736/a23cbf9e-563b-4b48-a173-ea4820f233c3">

### Usage

#### Download

```kotlin
dependencies {
    implementation("com.github.xe11:tagged-okhttp-logging-interceptor:-SNAPSHOT")
}
```

#### Install

```kotlin
import xe11.ok.logger.taggedHttpLoggingInterceptor

//...
OkHttpClient.Builder()
    // remove default logger
    // .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = Level.BODY })
    .addNetworkInterceptor(taggedHttpLoggingInterceptor(level = OkHttpLogLevel.BODY))
    .build()
```

[link-okhttp]: https://github.com/square/okhttp
[link-okhttp-logging-interceptor]: https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
