plugins {
    id("java-library")
    alias(libs.plugins.kotlinJvm)
    id("maven-publish")
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

val buildType = "release"

publishing {
    publications {
        create(buildType, MavenPublication::class.java) {
            groupId = "com.github.xe11"
            artifactId = "tagged-okhttp-logging-interceptor"

            afterEvaluate { from(components["java"]) }
        }
    }

    repositories {
        // ./gradlew publishReleasePublicationToMavenLocal to publish to ~/.m2

        maven {
            //  ./gradlew publishReleaseToMvnBuildDirRepository to publish to <module>/build/repo
            name = "MvnBuildDir"
            url = uri("${project.buildDir}/repo")
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
