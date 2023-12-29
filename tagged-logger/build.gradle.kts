plugins {
    id("java-library")
    alias(libs.plugins.kotlinJvm)
    // id("de.mannodermaus.android-junit5") version "1.10.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.network.okhttp)
    implementation(libs.network.okhttpLog)

    testRuntimeOnly(libs.testing.junit5.engine)
    testImplementation(libs.testing.junit5.api)
    testImplementation(libs.testing.junit5.params)

    testImplementation(libs.testing.assertj)
    testImplementation(libs.network.okhttpMockWebServer)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
