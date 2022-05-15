plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        applicationId = "ru.spbstu.sharedplanner"
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
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

        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(mapOf("path" to ":common")))
    implementation(project(mapOf("path" to ":auth")))
    implementation(project(mapOf("path" to ":calendar")))
    implementation(Deps.appLibraries)
    implementation(Deps.daggerDep)
    implementation(Deps.network)
    coreLibraryDesugaring(Deps.desugar_jdk)
    implementation(Deps.ok)
    implementation(Deps.googleAuth)
    kapt(Deps.daggerKapt)
    testImplementation(Deps.testLibraries)
    androidTestImplementation(Deps.androidTestLibraries)
    implementation(platform(Deps.firebaseBom))
    implementation(Deps.firebase)
}