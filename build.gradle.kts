buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
    }
}

plugins {
    alias(libs.plugins.sqldelight.plugin) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlin.serialization.plugin) apply false
    alias(libs.plugins.secrets.gradle.plugin) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}