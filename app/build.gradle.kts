plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp").version("1.8.22-1.0.11")
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    compileSdk = 34

    defaultConfig {
        applicationId = "de.westnordost.luftlinie"
        minSdk = 21
        targetSdk = 34
        versionCode = 6
        versionName = "1.5"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    namespace = "de.westnordost.luftlinie"
}

dependencies {
    val kotlinxCoroutinesVersion = "1.7.1"

    // Kotlin
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")

    // Android widgets
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    implementation("de.westnordost:osmfeatures-android:5.2")

    // View models
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // HTTP API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    // JSON parsing
    implementation("com.squareup.moshi:moshi:1.15.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    // Dependency injection
    implementation("io.insert-koin:koin-core:3.4.2")
    implementation("io.insert-koin:koin-android:3.4.2")
}

val bcp47ExportLanguages = setOf(
    "de","en","en-AU","en-GB","tr"
)
val presetsVersion = "v6.3.0"

tasks.register<UpdatePresetsTask>("updatePresets") {
    group = "luftlinie"
    version = presetsVersion
    languageCodes = bcp47ExportLanguages
    targetDir = "$projectDir/src/main/assets/osmfeatures"
}
