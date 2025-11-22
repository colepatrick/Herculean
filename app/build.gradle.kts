import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

//
// ───────────────────────────────────────────────────────────────
//  Load Local Properties (Gemini API Key)
// ───────────────────────────────────────────────────────────────
//
val localProps = Properties()
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localProps.load(localPropsFile.inputStream())
}
val geminiApiKey = localProps.getProperty("GEMINI_API_KEY") ?: ""

//
// ───────────────────────────────────────────────────────────────
//  Force Resolution of Testing Libraries (Avoid Version Conflicts)
// ───────────────────────────────────────────────────────────────
//
configurations.all {
    resolutionStrategy {
        force(libs.test.core)
        force(libs.ext.junit)
    }
}

//
// ───────────────────────────────────────────────────────────────
//  ANDROID CONFIGURATION
// ───────────────────────────────────────────────────────────────
//
android {
    namespace = "com.example.herculean"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.herculean"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Inject Gemini API Key → BuildConfig + resources
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        resValue("string", "gemini_api_key", geminiApiKey)

        // Test Runner
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Auto-grant all runtime permissions to Espresso tests
        testInstrumentationRunnerArguments += mapOf(
            "grantRuntimePermissions" to "true",
            "disableAnalytics" to "true"
        )
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

    // Allow Android resources inside unit tests
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

//
// ───────────────────────────────────────────────────────────────
//  UNIT TEST CONFIG ( /src/test/... )
// ───────────────────────────────────────────────────────────────
//
tasks.withType<Test> {
    useJUnit()  // Force JUnit 4
}

//
// ───────────────────────────────────────────────────────────────
//  DEPENDENCIES
// ───────────────────────────────────────────────────────────────
//
dependencies {

    // ---------------------------------------------------------
    // ============= UNIT TEST DEPENDENCIES =====================
    // /src/test/java
    // ---------------------------------------------------------
    testImplementation(libs.junit)
    testImplementation("org.json:json:20231013")


    // ---------------------------------------------------------
    // ============= ANDROID INSTRUMENTED TESTS =================
    // /src/androidTest/java
    // ---------------------------------------------------------
    androidTestImplementation(libs.test.core)     // androidx.test:core
    androidTestImplementation(libs.ext.junit)     // androidx.test.ext:junit
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rules)

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")

    // FragmentScenario (must be debugImplementation)
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")

    // Some test code expects classic JUnit
    androidTestImplementation("junit:junit:4.13.2")


    // ---------------------------------------------------------
    // ================== APP DEPENDENCIES ======================
    // ---------------------------------------------------------
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.constraintlayout)

    // Lifecycle
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Gson
    implementation("com.google.code.gson:gson:2.8.8")

    // CoordinatorLayout + Fragment library
    implementation(libs.coordinatorlayout)
    implementation(libs.fragment)

    // Glide for images
    implementation("com.github.bumptech.glide:glide:4.16.0")


    // ---------------------------------------------------------
    // ================ Google Gemini API =======================
    // ---------------------------------------------------------
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.google.guava:guava:33.4.1-android")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
}
