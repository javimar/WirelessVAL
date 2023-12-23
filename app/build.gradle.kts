import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun generateVersionCode(): Int {
    val formatter = DateTimeFormatter.ofPattern("yyMMddHH")
    val current = LocalDateTime.now().format(formatter)
    return current.toInt()
}

val datedVersionCode: Int = generateVersionCode()

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.sqldelight.plugin)
    alias(libs.plugins.google.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = libs.versions.appId.get()

    compileSdk = libs.versions.sdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.appId.get()
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.sdk.get().toInt()
        versionCode = datedVersionCode
        versionName = libs.versions.versionName.get()

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("boolean", "ENABLE_CRASHLYTICS", "false")

            // Disable Crashlytics upload for the debug build
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false // to disable mapping file uploads (default=true if minifying)
            }
            isMinifyEnabled = false
            isShrinkResources = false

            buildConfigField("Long", "BASE_API_TIMEOUT", "20L")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"))

            buildConfigField("Long", "BASE_API_TIMEOUT", "10L")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += listOf("/META-INF/{AL2.0,LGPL2.1}", "AndroidManifest.xml")
        }
    }
    secrets {
        propertiesFileName = "secrets.properties"
        defaultPropertiesFileName = "secrets.defaults.properties"
    }
}


sqldelight {
    databases {
        create("WirelessVALDatabase") {
            packageName.set("eu.javimar.wirelessval.sqldelight")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)
    //debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation(libs.androidx.lifecycle)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.preference)

    implementation(libs.maps.compose)
    implementation(libs.maps.compose.widgets)
    implementation(libs.android.maps.utils)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Serialization
    implementation(libs.kotlin.serialization.json)

    // SQLDELIGHT
    implementation(libs.sqldelight.android.driver)

    // KTOR
    implementation(libs.ktor.core)
    implementation(libs.ktor.android.client)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.client.okhttp)

    debugImplementation(libs.chucker)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}
