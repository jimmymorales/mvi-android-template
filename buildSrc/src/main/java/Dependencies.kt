object Sdk {
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 30
    const val COMPILE_SDK_VERSION = 30
}

object Versions {
    const val ANDROIDX_TEST_EXT = "1.1.2"
    const val ANDROIDX_TEST = "1.3.0"
    const val APPCOMPAT = "1.2.0"
    const val CONSTRAINT_LAYOUT = "2.0.4"
    const val CORE_KTX = "1.3.2"
    const val COROUTINES = "1.4.3"
    const val ESPRESSO_CORE = "3.3.0"
    const val FRAGMENT = "1.3.2"
    const val HILT = "2.33-beta"
    const val JUNIT = "4.13.2"
    const val KOTEST = "4.4.3"
    const val KTLINT = "0.40.0"
    const val LIFECYCLE = "2.4.0-alpha01"
    const val LOGGING_INTERCEPTOR = "3.9.0"
    const val MOSHI = "1.12.0"
    const val RETROFIT = "2.9.0"
    const val TIMBER = "4.7.1"
    const val TURBINE = "0.4.1"
    const val VIEWMODEL = "2.3.1"
}

object BuildPluginsVersion {
    const val DETEKT = "1.16.0"
    const val KTLINT = "10.0.0"
    const val VERSIONS_PLUGIN = "0.38.0"
}

object SupportLibs {
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val ANDROIDX_CONSTRAINT_LAYOUT =
        "com.android.support.constraint:constraint-layout:${Versions.CONSTRAINT_LAYOUT}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val ANDROIDX_FRAGMENT = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT}"
    const val ANDROIDX_LIFECYCLE_RUNTIME =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
    const val ANDROIDX_VIEWMODEL =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.VIEWMODEL}"
    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    const val MOSHI = "com.squareup.moshi:moshi:${Versions.MOSHI}"
    const val MOSHI_KOTLIN_CODEGEN = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.MOSHI}"
    const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
    const val RETROFIT_LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:${Versions.LOGGING_INTERCEPTOR}"
    const val RETROFIT_MOSHI_ADAPTER = "com.squareup.retrofit2:converter-moshi:${Versions.RETROFIT}"
    const val HILT = "com.google.dagger:hilt-android:${Versions.HILT}"
    const val HILT_CORE = "com.google.dagger:hilt-core:${Versions.HILT}"
    const val HILT_COMPILER = "com.google.dagger:hilt-compiler:${Versions.HILT}"
    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"
}

object TestingLib {
    const val COROUTINES_TEST =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val KOTEST_ASSERTIONS_CORE = "io.kotest:kotest-assertions-core:${Versions.KOTEST}"
    const val KOTEST_PROPERTY_TESTING = "io.kotest:kotest-property:${Versions.KOTEST}"
    const val TURBINE = "app.cash.turbine:turbine:${Versions.TURBINE}"

}

object AndroidTestingLib {
    const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
}
