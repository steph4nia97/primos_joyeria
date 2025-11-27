plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.primosjoyeria"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.primosjoyeria"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    // Java 17 para Java y Kotlin (KSP)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures { compose = true }
    // NO uses composeOptions con Kotlin 2.x
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // JUnit 5 para tests de unidad (JVM)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    // Kotest (matchers bonitos)
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")

    // MockK para mocks en Kotlin
    testImplementation("io.mockk:mockk:1.13.12")

    // Coroutines test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Compose UI Test (instrumented)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.4")
    testImplementation(kotlin("test"))
}
tasks.withType<Test> {
    useJUnitPlatform()
}