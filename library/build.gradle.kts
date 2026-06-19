plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    id("maven-publish")
    id("signing")
    // Add the specialized central portal publisher plugin
    id("com.vanniktech.maven.publish") version "0.36.0"
}

android {
    namespace = "com.ongshok.iconify"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}

// --- Maven Central Publishing Configuration ---
mavenPublishing {
    // Defines your token coordinates
    coordinates("com.ongshok", "iconify", "1.0.0")

    pom {
        name.set("Iconify Compose")
        description.set("An asynchronous icon loading library for Jetpack Compose using Iconify API.")
        url.set("https://github.com/ongshok/com.ongshok.iconifycompose")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("dzeroone")
                name.set("Najmul Hosain")
            }
        }
        scm {
            url.set("https://github.com/ongshok/com.ongshok.iconifycompose")
        }
    }

    // Configures publishing specifically for the new Sonatype Central Portal
//     publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    publishToMavenCentral(automaticRelease = true)

    // Automatically handles signing via your GPG configuration
    signAllPublications()
}