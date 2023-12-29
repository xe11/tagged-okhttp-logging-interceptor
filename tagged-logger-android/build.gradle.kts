plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.junit5Android)
    id("maven-publish")
}

val buildType = "release"

android {
    namespace = "xe11.ok.logging"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant(buildType) {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(buildType) {
            groupId = "com.github.xe11"
            artifactId = "tagged-okhttp-logging-interceptor"

            afterEvaluate {
                from(components[buildType])
            }
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

dependencies {
    api(project(":tagged-logger"))

    implementation(libs.network.okhttp)
    implementation(libs.network.okhttpLog)

    testRuntimeOnly(libs.testing.junit5.engine)
    testImplementation(libs.testing.junit5.api)
    testImplementation(libs.testing.junit5.params)

    testImplementation(libs.testing.assertj)
    testImplementation(libs.network.okhttpMockWebServer)
}
