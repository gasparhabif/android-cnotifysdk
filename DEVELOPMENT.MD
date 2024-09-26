## Publishing
1. Add code and increment version in `CNotifySDK/build.gradle.kts` and any other locations
2. Push a new git tag corresponding to that version
3. Run `gradle wrapper` in the root of this project


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

https://medium.com/@zurcher/create-and-distribute-your-own-android-library-after-reading-this-post-c3187a2a0691#.f9rq4y8kq

https://jitpack.io/#gasparhabif/android-cnotifysdk/main
