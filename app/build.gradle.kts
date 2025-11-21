import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localProps.load(localPropsFile.inputStream())
}

val geminiApiKey = localProps.getProperty("GEMINI_API_KEY") ?: ""

android {
    namespace = "com.example.herculean"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.example.herculean"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        resValue("string", "gemini_api_key", geminiApiKey)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:5000/\"") // emulator -> host
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://api.yourdomain.com/\"") // database URL
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    // ✅ This allows unit tests in /src/test/java to run Android-like code (needed by org.json)
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

// ✅ This tells Gradle explicitly to use JUnit 4
tasks.withType<Test> {
    useJUnit()
}

dependencies {
    // --- Unit testing dependencies ---
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("org.json:json:20231013")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.google.code.gson:gson:2.8.8")

    // Google Gemini API
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.google.guava:guava:33.4.1-android")
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    implementation(libs.coordinatorlayout)
    implementation(libs.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
}