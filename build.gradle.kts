// AGP 9 ya trae el soporte de Kotlin integrado: el plugin 'kotlin.android' sobra.
// https://kotl.in/gradle/agp-built-in-kotlin
plugins {
    alias(libs.plugins.sqldelight.plugin) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.serialization.plugin) apply false
    alias(libs.plugins.kotlin.compose.plugin) apply false
}

tasks.register("clean", Delete::class) {
    description = "Clean"
    delete(rootProject.layout.buildDirectory)
}