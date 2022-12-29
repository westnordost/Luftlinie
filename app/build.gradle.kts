plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileSdk = 33

    defaultConfig {
        applicationId = "de.westnordost.luftlinie"
        minSdk = 21
        targetSdk = 33
        versionCode = 3
        versionName = "1.2"
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
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Android widgets
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.fragment:fragment-ktx:1.3.4")

    implementation("de.westnordost:osmfeatures-android:2.1")

    // View models
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    // HTTP API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    // JSON parsing
    implementation("com.squareup.moshi:moshi:1.9.3")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.3")

    // Dependency injection
    implementation("io.insert-koin:koin-core:2.2.3")
    implementation("io.insert-koin:koin-android:2.2.3")
    implementation("io.insert-koin:koin-androidx-viewmodel:2.2.3")
    implementation("io.insert-koin:koin-androidx-fragment:2.2.3")
}

val bcp47ExportLanguages = setOf(
    "de","en","en-AU","en-GB"
)
val presetsVersion = "v5.1.1"

tasks.register<UpdatePresetsTask>("updatePresets") {
    group = "luftlinie"
    version = presetsVersion
    languageCodes = bcp47ExportLanguages
    targetDir = "$projectDir/src/main/assets/osmfeatures"
}
