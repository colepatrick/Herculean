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

// Force the use of the correct testing library versions to resolve conflicts
configurations.all {
    resolutionStrategy {
        force(libs.test.core)
        force(libs.ext.junit)
    }
}

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
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    testImplementation(libs.junit)
    testImplementation("org.json:json:20231013")

    // --- Android Instrumentation testing dependencies ---
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.espresso.core)


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

    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Graphing Views
    implementation("com.jjoe64:graphview:4.2.2")
}