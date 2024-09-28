// import com.vanniktech.maven.publish.AndroidSingleVariantLibrary

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.29.0" apply false
    id("com.gradleup.nmcp") version "0.0.8" apply false
}

android {
    namespace = "me.cnotify.cnotify_android_sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        aarMetadata {
            minCompileSdk = 24
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Firebase Messaging with BOM to manage versions
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.gson)

    // Test libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "me.cnotify"
            artifactId = "cnotify_android_sdk"
            version = "0.3.2"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = "CNotifySDK"
                description =
                    "This SDK is used to connect the integration with cnotify.me. It provides functionality for handling push notifications and integrating with the cnotify.me service."
                url = "https://cnotify.me"
                licenses {
                    license {
                        name = "MIT"
                        url = "https://github.com/gasparhabif/android-cnotifysdk/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "tlofano"
                        name = "Tomás Lofano"
                        email = "tomas@eruka.tech"
                    }
                    developer {
                        id = "gaspihabif"
                        name = "Gaspar Habif"
                        email = "gaspar@eruka.tech"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/gasparhabif/android-cnotifysdk.git"
                    developerConnection = "scm:git:ssh://github.com/gasparhabif/android-cnotifysdk.git"
                    url = "https://cnotify.me"
                }
            }
        }
    }
}

// mavenPublishing {
//   configure(
//       AndroidSingleVariantLibrary(
//     // the published variant
//     variant = "release",
//     // whether to publish a sources jar
//     sourcesJar = true,
//     // whether to publish a javadoc jar
//     publishJavadocJar = true,
//   )
//   )
// }

// mavenPublishing {
//   configure(GradlePublishPlugin())
// }

// tasks.register("publishAllPublicationsToCentralPortal") {
//     dependsOn("publish")
// }
