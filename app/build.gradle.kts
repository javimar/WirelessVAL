import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

fun generateVersionCode(): Int {
    val formatter = DateTimeFormatter.ofPattern("yyMMddHH")
    val current = LocalDateTime.now().format(formatter)
    return current.toInt()
}

val datedVersionCode: Int = generateVersionCode()

/**
 * Secreto local (API key de Maps), leido a mano de secrets.properties. El
 * secrets-gradle-plugin esta muerto (sin release desde feb-2022, issue #41 abierto) y bajo
 * AGP 9 deja el marcador del manifest vacio en silencio. Mismo patron que Swim-Manager.
 * secrets.properties esta gitignoreado; secrets.defaults.properties SI va a git.
 */
fun loadSecret(key: String): String {
    val secretsFile = rootProject.file("secrets.properties")
    val defaultsFile = rootProject.file("secrets.defaults.properties")
    val propertiesFile = if (secretsFile.exists()) secretsFile
        else if (defaultsFile.exists()) defaultsFile
        else return ""

    val properties = Properties()
    propertiesFile.inputStream().use { properties.load(it) }
    return properties.getProperty(key, "")
}

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.sqldelight.plugin)
    alias(libs.plugins.kotlin.serialization.plugin)
    alias(libs.plugins.kotlin.compose.plugin)
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

        manifestPlaceholders["API_KEY"] = loadSecret("API_KEY")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false

            buildConfigField("Long", "BASE_API_TIMEOUT", "20L")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))

            buildConfigField("Long", "BASE_API_TIMEOUT", "10L")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += listOf("/META-INF/{AL2.0,LGPL2.1}", "AndroidManifest.xml")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
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
    // @Preview vive en ui-tooling-preview y lo usan 21 ficheros de src/main: hace falta en
    // TODOS los build types. El visor de previews (ui-tooling) solo hace falta en debug.
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)
    implementation(libs.compose.nav)

    implementation(libs.androidx.lifecycle)

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
    // Patron oficial de Chucker, el mismo que My-Fallas-Guide: en release entra el
    // artefacto no-op, con la misma API pero vacia. Asi el codigo vive en src/main sin
    // partir source sets, y en produccion no se registra ni una peticion.
    releaseImplementation(libs.chucker.no.op)
}
