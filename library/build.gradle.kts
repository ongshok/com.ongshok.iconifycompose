plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    id("maven-publish")
    id("signing")
}

// Ensure sources are packaged with your library
java {
    withSourcesJar()
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

    publishing {
        singleVariant("release") {
            withSourcesJar() // Automatically packages your source code
        }
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

extensions.configure<PublishingExtension> {
    publications {
        create<MavenPublication>("release") {
            // Replace with your verified namespace details
            groupId = "com.ongshok"
            artifactId = "iconify"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

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
                        name.set("Md. Najmul Hosain")
                        email.set("developer.zeroone@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ongshok/com.ongshok.iconifycompose.git")
                    developerConnection.set("scm:git:ssh://github.com/ongshok/com.ongshok.iconifycompose.git")
                    url.set("https://github.com/ongshok/com.ongshok.iconifycompose")
                }
            }
        }
    }
}

extensions.configure<SigningExtension> {
    useInMemoryPgpKeys(
        System.getenv("GPG_PRIVATE_KEY"),
        System.getenv("GPG_PASSPHRASE")
    )
    val publishing = extensions.getByType<PublishingExtension>()
    sign(publishing.publications["release"])
}