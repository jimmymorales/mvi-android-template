version = LibraryKotlinCoordinates.LIBRARY_VERSION

plugins {
    id("java-library")
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish")
    publish
}

dependencies {
    implementation(SupportLibs.HILT_CORE)
    kapt(SupportLibs.HILT_COMPILER)
    implementation(SupportLibs.RETROFIT)
    implementation(SupportLibs.RETROFIT_MOSHI_ADAPTER)
    implementation(SupportLibs.MOSHI)
    implementation(SupportLibs.RETROFIT_LOGGING_INTERCEPTOR)
    kapt(SupportLibs.MOSHI_KOTLIN_CODEGEN)

    testImplementation(TestingLib.JUNIT)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
