import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

fun getAppKey(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}

android {
    namespace = "com.ping.app"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.ping.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["NAVER_MAP_CLIENT_ID"] = getAppKey("NAVER_MAP_CLIENT_ID")
        buildConfigField("String", "CHAT_GPT", getAppKey("CHAT_GPT"))
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(AndroidX.CORE_KTX)
    implementation(AndroidX.APPCOMPAT)
    implementation(AndroidX.CONSTRAINT_LAYOUT)
    implementation(AndroidX.NAVIGATION_FRAGMENT)
    implementation(AndroidX.NAVIGATION_UI)
    implementation(AndroidX.DATASTORE)
    implementation(AndroidX.SPLASH_SCREEN)
    implementation(Google.MATERIAL)
    implementation(Google.GOOGLE_PLAY_SERVICE_LOCATION)
    implementation(platform(Google.FIREBASE_BOM))
    implementation(platform(Google.FIREBASE_ANALYTICS))
    testImplementation(UnitTest.TEST_JUNIT_CORE)
    testImplementation(UnitTest.COROUTINE_TEST)
    androidTestImplementation(UnitTest.TEST_JUNIT)
    androidTestImplementation(UnitTest.ESPRESSO)
    implementation(Naver.NAVER_MAP)
    implementation(Google.FIREBASE_AUTH)
    implementation(Google.GOOGLE_PLAY_SERVICE_AUTH)
    implementation(Google.FIREBASE_FIRESTORE)
    implementation(Google.FIREBASE_CLOUD_FUNCTION)
    implementation(Google.FIREBASE_CLOUD_MESSAGING)
    implementation(Google.CREDENTIAL_PLAY_SERVICE)
    implementation(Google.CREDENTIAL)
    implementation(Google.GOOGLE_ID)
    implementation(Permission.TED_NORMAL)
    implementation(Permission.TED_COROUTINE)
    implementation(Square.GLIDE)
    implementation(Square.RETROFIT2)
    implementation(Square.OKHTTP3)
    implementation(Square.OKHTTP3_LOGGING)
    implementation(Square.RETROFIT2_CONVERTER_GSON)
    implementation(Airbnb.Lottie)
}