plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.junit5Android)
}

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
