pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            // https://developer.android.com/studio/releases/gradle-plugin
            // Please update Lint version whenever you update Android Gradle plugin
            version("androidGradlePlugin", "8.2.0")

            // https://kotlinlang.org/docs/releases.html#release-details
            version("kotlin", "1.8.21")

            // https://github.com/square/okhttp/blob/master/CHANGELOG.md
            version("okhttp", "4.11.0")

            // https://assertj.github.io/doc/#assertj-core-release-notes
            version("assertj", "3.24.2")

            // https://junit.org/junit5/docs/snapshot/release-notes/
            version("junit5", "5.10.1")
            // https://github.com/mannodermaus/android-junit5/releases
            version("junit5Android", "1.10.0.0")

            // https://github.com/mockito/mockito/releases
            version("mockito", "4.11.0")
            // https://github.com/mockito/mockito-kotlin/releases
            version("mockitoKotlin", "4.1.0")

            library("androidGradlePlugin", "com.android.tools.build", "gradle")
                .versionRef("androidGradlePlugin")

            library("network_okhttp", "com.squareup.okhttp3", "okhttp")
                .versionRef("okhttp")
            library("network_okhttpLog", "com.squareup.okhttp3", "logging-interceptor")
                .versionRef("okhttp")
            library("network_okhttpMockWebServer", "com.squareup.okhttp3", "mockwebserver")
                .versionRef("okhttp")

            library("kotlin_gradlePlugin", "org.jetbrains.kotlin", "kotlin-gradle-plugin")
                .versionRef("kotlin")

            library("testing_junit5_api", "org.junit.jupiter", "junit-jupiter-api")
                .versionRef("junit5")
            library("testing_junit5_engine", "org.junit.jupiter", "junit-jupiter-engine")
                .versionRef("junit5")
            library("testing_junit5_params", "org.junit.jupiter", "junit-jupiter-params")
                .versionRef("junit5")
            bundle(
                "testing_junit5",
                listOf("testing_junit5_api", "testing_junit5_params"),
            )

            library("testing_mockito_core", "org.mockito", "mockito-core")
                .versionRef("mockito")
            library("testing_mockito_inline", "org.mockito", "mockito-inline")
                .versionRef("mockito")
            library("testing_mockito_kotlin", "org.mockito.kotlin", "mockito-kotlin")
                .versionRef("mockitoKotlin")
            bundle(
                "testing_mockito",
                listOf(
                    "testing_mockito_core",
                    "testing_mockito_inline",
                    "testing_mockito_kotlin",
                ),
            )

            library("testing_assertj", "org.assertj", "assertj-core")
                .versionRef("assertj")


            plugin("kotlinAndroid", "org.jetbrains.kotlin.android")
                .versionRef("kotlin")
            plugin("kotlinJvm", "org.jetbrains.kotlin.jvm")
                .versionRef("kotlin")

            plugin("androidApplication", "com.android.application")
                .versionRef("androidGradlePlugin")
            plugin("androidLibrary", "com.android.library")
                .versionRef("androidGradlePlugin")

            plugin("junit5Android", "de.mannodermaus.android-junit5")
                .versionRef("junit5Android")
        }
    }
}
rootProject.name = "tagged-okhttp-logging-interceptor"
include(":tagged-logger")
