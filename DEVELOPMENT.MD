# CNotifySDK Development

## Status
[JitPack](https://jitpack.io/#gasparhabif/android-cnotifysdk/main)

## Publishing
1. Add code and increment version in `CNotifySDK/build.gradle.kts`, `gradle.properties` and any other locations
2. Push a new git tag corresponding to that version
3. Run `gradle wrapper` in the root of this project (This will publish it to JitPack)
4. Run `./gradlew publishAllPublicationsToCentralPortal` in the root of this project (This will publish it to MavenCentral)


## Add it to your app

Fragment code to request notification permission and initialize the CNotifySDK:

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        // Request push notification permission
        requestNotificationPermission()

        fun getApplicationContext(): Context {
            return applicationContext
        }
        CNotifySDK.getInstance(getApplicationContext(), true)
        ...
    }

    private fun requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }
    }
    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 19971999
    }
```


## References
- [Developing a library](https://medium.com/@zurcher/create-and-distribute-your-own-android-library-after-reading-this-post-c3187a2a0691#.f9rq4y8kq)
- [Publishing to MavenCentral](https://medium.com/@iRYO400/how-to-upload-your-android-library-to-maven-central-central-portal-in-2024-af7348742247)


