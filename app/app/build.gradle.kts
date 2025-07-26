import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dag.mobinchapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dag.mobinchapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val oneinchKey: String? = System.getenv("ONEINCH_KEY")
        val openAIKey: String? = System.getenv("OPEN_AI_KEY")
        
        if (oneinchKey != null && openAIKey != null) {
            buildConfigField("String", "oneinchKey", "\"$oneinchKey\"")
            buildConfigField("String", "openAIKey", "\"$openAIKey\"")
        } else {
            val localProperties = Properties()
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localProperties.load(FileInputStream(localPropertiesFile))
                buildConfigField("String", "oneinchKey", "\"${localProperties.getProperty("oneinch_key")}\"")
                buildConfigField("String", "openAIKey", "\"${localProperties.getProperty("open_ai_key")}\"")
            } else {
                throw GradleException("Missing both environment variables and local.properties for API keys.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.vision.internal.vkp)
    testImplementation(libs.junit)
    implementation(libs.androidx.material.icons.extended)

    // Test dependencies with fixed versions
    androidTestImplementation(platform(libs.androidx.compose.bom.v20240200))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.work.runtime.ktx)

    //Multidex
    implementation(libs.androidx.multidex)

    //Network
    implementation(libs.converter.gson)
    implementation(libs.ktor.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.json)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.serialization.json.jvm)

    //Datastore
    implementation(libs.androidx.datastore.preferences)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.analytics)

    //Chucker
    debugImplementation(libs.library)

    //Hilt
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    androidTestImplementation(libs.hilt.android.testing)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.hilt.android.compiler)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.work)

    //Security
    implementation(libs.androidx.security.crypto)

    implementation(platform(libs.kotlin.bom))
    implementation(libs.kotlinx.metadata.jvm)

    implementation(libs.metamask.android.sdk)

}
