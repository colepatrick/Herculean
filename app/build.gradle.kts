plugins {
    alias(libs.plugins.android.application)
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
    implementation(libs.coordinatorlayout)

    // --- Instrumented Android tests ---
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
