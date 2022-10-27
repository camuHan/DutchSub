plugins {
    id ("kotlin-kapt")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("dagger.hilt.android.plugin")
}

android {
    compileSdk = Apps.compileSdk

    defaultConfig {
        minSdk = Apps.minSdk
        targetSdk = Apps.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
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
}

dependencies {
    implementation(project(":domain"))

    implementation (Firebase.AUTH)
    implementation (Firebase.AUTH_KTX)
    implementation (Firebase.FIRE_STORE)
    implementation (Firebase.STORAGE)
    implementation (Firebase.STORAGE_KTX)

    implementation (JetBrain.COROUTINE)
    implementation (JetBrain.COROUTINE_SERVICE)

    implementation (AndroidX.ROOM)
    implementation (AndroidX.ROOM_KTX)
    implementation (AndroidX.ROOM_RUNTIME)
    kapt (AndroidX.ROOM_COMPILER)

    implementation (Google.GSON)
    implementation (DaggerHilt.DAGGER_HILT)
    kapt (DaggerHilt.DAGGER_HILT_COMPILER)

    implementation (AndroidX.CORE)
    implementation (AndroidX.APP_COMPAT)
    implementation (Google.MATERIAL)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}