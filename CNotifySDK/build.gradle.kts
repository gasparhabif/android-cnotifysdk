plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
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
            version = "0.0.3"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = "CNotifySDK"
                description =
                    "This SDK is used to connect the integration with cnotify.me. It provides functionality for handling push notifications and integrating with the cnotify.me service."
                url = "https://cnotify.me"
                properties = mapOf(
                    "myProp" to "value",
                    "prop.with.dots" to "anotherValue"
                )
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
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
