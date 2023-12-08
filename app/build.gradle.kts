import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun generateVersionCode(): Int {
    val formatter = DateTimeFormatter.ofPattern("yyMMddHH")
    val current = LocalDateTime.now().format(formatter)
    return current.toInt()
}

val datedVersionCode: Int = generateVersionCode()
val versionNamePrefix: String by project
val versionNameSuffix: String by project
val appId: String by project
val wirelessValVersion = versionNamePrefix + versionNameSuffix

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("app.cash.sqldelight")
}

android {
    namespace = appId

    compileSdk = 34

    defaultConfig {
        applicationId = appId
        minSdk = 26
        targetSdk = 34
        versionCode = datedVersionCode
        versionName = wirelessValVersion

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

        // TO DELETE
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
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

    implementation("androidx.core:core-ktx:1.12.0")

    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.activity:activity-compose")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    implementation("com.google.accompanist:accompanist-permissions:0.23.1")

    implementation("androidx.preference:preference-ktx:1.2.1")

    implementation("com.google.maps.android:maps-compose:2.14.1")
    implementation("com.google.maps.android:maps-compose-widgets:2.14.1")
    implementation("com.google.maps.android:android-maps-utils:3.5.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    // SQLDELIGTH
    implementation("app.cash.sqldelight:android-driver:2.0.0")

    // KTOR
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-android:2.3.6")
    implementation("io.ktor:ktor-client-serialization:2.3.6")
    implementation("io.ktor:ktor-client-logging:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-client-okhttp:2.3.6")

    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")











    // PARA BORRAR
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    //noinspection LifecycleAnnotationProcessorWithJava8
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.6.2")
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.palette:palette:1.0.0")
    implementation ("com.github.GrenderG:Toasty:1.4.2")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
}
