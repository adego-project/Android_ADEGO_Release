import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.seogaemo.android_adego"
    compileSdk = 34

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }

    val BASE_URL = localProperties.getProperty("BASE_URL") ?: ""

    val KAKAO_NATIVE_KEY = localProperties.getProperty("KAKAO_NATIVE_KEY") ?: ""
    val MANIFESTS_KAKAO_NATIVE_KEY = localProperties.getProperty("MANIFESTS_KAKAO_NATIVE_KEY") ?: ""

    val CLIENT_ID = localProperties.getProperty("CLIENT_ID") ?: ""

    val MAPS_API_KEY = localProperties.getProperty("MAPS_API_KEY") ?: ""

    defaultConfig {
        applicationId = "com.seogaemo.android_adego"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"$BASE_URL\"")
        resValue("string", "BASE_URL", BASE_URL)

        buildConfigField("String", "KAKAO_NATIVE_KEY", "\"$KAKAO_NATIVE_KEY\"")
        resValue("string", "KAKAO_NATIVE_KEY", KAKAO_NATIVE_KEY)
        buildConfigField("String", "MANIFESTS_KAKAO_NATIVE_KEY", "\"$MANIFESTS_KAKAO_NATIVE_KEY\"")
        resValue("string", "MANIFESTS_KAKAO_NATIVE_KEY", MANIFESTS_KAKAO_NATIVE_KEY)

        buildConfigField("String", "CLIENT_ID", "\"$CLIENT_ID\"")
        resValue("string", "CLIENT_ID", CLIENT_ID)

        buildConfigField("String", "MAPS_API_KEY", "\"$MAPS_API_KEY\"")
        resValue("string", "MAPS_API_KEY", MAPS_API_KEY)
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
    viewBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)

    implementation(libs.v2.all)

    implementation(libs.googleid)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.credentials)

    implementation(libs.glide)

    implementation(libs.play.services.maps)

    implementation(libs.material.calendarview)
    implementation(libs.threetenabp)

    implementation(libs.circleimageview)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.play.services.tasks)

    implementation(libs.play.services.location)
}