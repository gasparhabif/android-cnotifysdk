import com.google.gson.Gson

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}

android {
    namespace = "me.cnotify.cnotify_android_sdk"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24
        aarMetadata {
            minCompileSdk = 24
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // We parse the google-services.json file in build-time
        buildConfigField("String", "CNTFY_FIREBASE_PROJECT_ID", "\"-1\"")
        buildConfigField("String", "CNTFY_FIREBASE_API_KEY", "\"-1\"")
        buildConfigField("String", "CNTFY_FIREBASE_APP_ID", "\"-1\"")
        buildConfigField("String", "CNTFY_FIREBASE_MESSAGING_SENDER_ID", "\"-1\"")


        val googleServicesJsonFile = file("../app/src/main/google-services.json")
        if (googleServicesJsonFile.exists()) {
            val gson = Gson()
            try {
                val jsonText = googleServicesJsonFile.readText()
                val mapType = object : com.google.gson.reflect.TypeToken<Map<String, Any>>() {}.type
                val jsonMap: Map<String, Any> = gson.fromJson(jsonText, mapType)
                val projectId = (jsonMap["project_info"] as Map<*, *>)["project_id"] as String
                val gClient = (jsonMap["client"] as List<*>)[0] as Map<*, *>
                val gRawApiKey = gClient["api_key"] as List<*>
                val gApiKey = gRawApiKey[0] as Map<*, *>
                val apiKey = gApiKey["current_key"] as String
                val gClientInfo = gClient["client_info"] as Map<*, *>
                val appId = gClientInfo["mobilesdk_app_id"] as String
                val messagingSenderId = (jsonMap["project_info"] as Map<*, *>)["project_number"] as String


                buildConfigField("String", "CNTFY_FIREBASE_PROJECT_ID", "\"$projectId\"")
                buildConfigField("String", "CNTFY_FIREBASE_API_KEY", "\"$apiKey\"")
                buildConfigField("String", "CNTFY_FIREBASE_APP_ID", "\"$appId\"")
                buildConfigField("String", "CNTFY_FIREBASE_MESSAGING_SENDER_ID", "\"$messagingSenderId\"")
            } catch (e: Exception) {
                throw RuntimeException("Error al parsear google-services.json: ${e.message}")
            }
        }
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

    // With this dependency, the SDK will be able to parse the GoogleServices.json
    implementation(libs.gson.v210)
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
                // properties = mapOf(
                //     "myProp" to "value",
                //     "prop.with.dots" to "anotherValue"
                // )
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
