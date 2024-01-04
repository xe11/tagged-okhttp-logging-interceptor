import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED

plugins {
    id("java-library")
    alias(libs.plugins.kotlinJvm)
    id("maven-publish")
    jacoco
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
        events(FAILED, SKIPPED, PASSED)
        showExceptions = true
        exceptionFormat = FULL
        showCauses = true
        showStackTraces = true
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}
