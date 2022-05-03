plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            buildConfigField("String", "ENDPOINT", "\"https://blogin.ru.com/\"")
            buildConfigField("String", "REFRESH_ENDPOINT", "\"https://blogin.ru.com/api/v1/auth/refresh\"")
        }
        getByName("release") {
            buildConfigField("String", "ENDPOINT", "\"https://blogin.ru.com/\"")
            buildConfigField("String", "REFRESH_ENDPOINT", "\"https://blogin.ru.com/api/v1/auth//refresh\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(Deps.appLibraries)
    implementation(Deps.daggerDep)
    implementation(Deps.network)
    kapt(Deps.daggerKapt)
    testImplementation(Deps.testLibraries)
    androidTestImplementation(Deps.androidTestLibraries)
    kapt(Deps.glideKapt)
}