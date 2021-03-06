plugins {
    `kotlin-dsl`
}
repositories {
    jcenter()
    google()
}

object Plugins {
    const val AGP = "4.2.2"
    const val DOKKA = "1.4.32"
    const val KOTLIN = "1.5.21"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Plugins.KOTLIN}")
    implementation("com.android.tools.build:gradle:${Plugins.AGP}")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:${Plugins.DOKKA}")
    implementation("org.jetbrains.dokka:dokka-core:${Plugins.DOKKA}")
}
