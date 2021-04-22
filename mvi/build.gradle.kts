plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(Sdk.COMPILE_SDK_VERSION)

    defaultConfig {
        minSdkVersion(Sdk.MIN_SDK_VERSION)
        targetSdkVersion(Sdk.TARGET_SDK_VERSION)

        versionCode = LibraryAndroidCoordinates.LIBRARY_VERSION_CODE
        versionName = LibraryAndroidCoordinates.LIBRARY_VERSION

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(SupportLibs.ANDROIDX_APPCOMPAT)
    implementation(SupportLibs.ANDROIDX_CORE_KTX)
    implementation(SupportLibs.ANDROIDX_FRAGMENT)
    implementation(SupportLibs.ANDROIDX_LIFECYCLE_RUNTIME)
    implementation(SupportLibs.ANDROIDX_VIEWMODEL)

    implementation(SupportLibs.COROUTINES)

    implementation(SupportLibs.TIMBER)

    testImplementation(TestingLib.COROUTINES_TEST)
    testImplementation(TestingLib.JUNIT)
    testImplementation(TestingLib.KOTEST_ASSERTIONS_CORE)
    testImplementation(TestingLib.KOTEST_PROPERTY_TESTING)
    testImplementation(TestingLib.TURBINE)

    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_RUNNER)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT)
}
