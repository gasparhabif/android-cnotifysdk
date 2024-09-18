package me.cnotify.cnotify_android_sdk

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.InputStreamReader

class CNotifySDK private constructor(
    private val getContext: () -> Context,
    private val testingMode: Boolean = false,
    private val filePath: String?,
) {

    companion object {
        @Volatile
        private var INSTANCE: CNotifySDK? = null

        // fun getInstance(file: String, testing: Boolean = false): CNotifySDK {
        fun getInstance(context: Context, testing: Boolean = false, googleServiceFilePathOverride: String? = null): CNotifySDK {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CNotifySDK({ context }, testing, googleServiceFilePathOverride).also { INSTANCE = it }
            }
        }
    }

    private var subscribedToTopics = false

    init {
        initializeFirebase()
    }

    private fun initializeFirebase() {
        printCNotifySDK("Initializing (Version: 0.0.1)")
        if (FirebaseApp.getApps(getContext()).isEmpty()) {
            printCNotifySDK("Configuring Firebase app")
            FirebaseApp.initializeApp(getContext(), getFirebaseOptions())
//            try {
//                FirebaseApp.initializeApp(getContext())
//            } catch (e: Exception) {
//                throw IllegalArgumentException("A google-services.json must be included in the root of the app. Failed to load Firebase options, file not found: ${e.message}", e)
//            }
        } else {
            printCNotifySDK("Firebase app is already configured.")
        }
        requestPermissions()
    }

     private fun getFirebaseOptions(): FirebaseOptions {
         try {
             val context = getContext()
            // Check if the file exists in the assets folder
            val fileList = context.assets.list("") ?: throw FileNotFoundException("Assets folder is empty. It must have the google-services.json file.")

            val fileToUse = filePath ?: "google-services.json"
            if (!fileList.contains(fileToUse)) {
                // Throw an exception if the file doesn't exist
                throw FileNotFoundException("The file $fileToUse does not exist in the assets folder.")
            }


             val file = context.assets.open(fileToUse)
             val reader = InputStreamReader(file)
             val gson = Gson()
             val json = gson.fromJson(reader, JsonObject::class.java)

             // Parse the required fields from the JSON
             val projectInfo = json.getAsJsonObject("project_info")
             val projectId = projectInfo.get("project_id").asString
             val messagingSenderId = projectInfo.get("project_number").asString

             val client = json.getAsJsonArray("client")
                 .get(0).asJsonObject
             val clientInfo = client.getAsJsonObject("client_info")
             val apiKey = client.getAsJsonArray("api_key").get(0).asJsonObject
                 .get("current_key").asString
             val appId = clientInfo.get("mobilesdk_app_id").asString

             // Initialize Firebase using the parsed data
             return FirebaseOptions.Builder()
                 .setApiKey(apiKey)
                 .setProjectId(projectId)
                 .setApplicationId(appId)
                 .setGcmSenderId(messagingSenderId)
                 .build()
         } catch (e: Exception) {
             if(filePath == null) {
                throw IllegalArgumentException("Make sure a google-services.json is included in the root of the app. Error: ${e.message}", e)
             } else {
                throw IllegalArgumentException("Failed to load Firebase options, file not found: ${e.message}", e)
             }
         }
     }

    // Request Notification Permissions
    private fun requestPermissions() {
//        val notificationManager = NotificationManagerCompat.from(context)
//        if (!notificationManager.areNotificationsEnabled()) {
//            printCNotifySDK("Notifications permissions not granted, request permissions to user and init the SDK again.")
//        } else {
//            // Automatically register for notifications if permission is granted
//            printCNotifySDK("Notifications permissions granted")
            registerForRemoteNotifications()
            subscribeToTopics()
//        }
    }

    private fun registerForRemoteNotifications() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    private fun subscribeToTopics() {
        if (subscribedToTopics) {
            printCNotifySDK("Tried to subscribe to topics but already subscribed")
            return
        }

        printCNotifySDK("Starting topic subscription")
        val generator = CNotifyTopicGenerator()
        val topics = generator.getTopics(language = getLang(), country = getCountry(), appVersion = getAppVersion())
        topics.forEach { topic ->
            printCNotifySDK("Subscribing to topic: $topic")
            subscribeTopic(topic)
        }

        if (testingMode) {
            subscribeTopic("testing-debug")
        }

        subscribedToTopics = true
        printCNotifySDK("Topic subscription ended")
    }

    private fun subscribeTopic(topic: String, completion: ((Exception?) -> Unit)? = null) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    completion?.invoke(task.exception)
                } else {
                    printCNotifySDK("Subscribed to topic: $topic")
                }
            }
    }

    private fun getLang(): String {
        return getContext().resources.configuration.locales[0].language ?: "en"
    }

    private fun getCountry(): String {
        return getContext().resources.configuration.locales[0].country ?: "??"
    }

    private fun getAppVersion(): String {
        return try {
            getContext().packageManager.getPackageInfo(getContext().packageName, 0).versionName ?: "0.0"
        } catch (e: Exception) {
            "0.0"
        }
    }

    private fun printCNotifySDK(message: String) {
        Log.d("CNotifySDK - Android", "[CNotifySDK] $message")
    }
}

class CNotifyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("CNotifySDK", "Firebase registration token received: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("CNotifySDK", "Received notification: ${remoteMessage.data}")
    }
}