package com.android.sdk_integration_guide

import android.app.Application
import android.os.Build
import android.util.Log
import com.sign3.public_classes.Sign3Intelligence
import com.sign3.public_classes.common.Options

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // The SDK requires API 23+. Guard the call if your app's minSdk is lower.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val options = Options.Builder()
                .setClientId(BuildConfig.SIGN3_CLIENT_ID)
                .setClientSecret(BuildConfig.SIGN3_CLIENT_SECRET)
                .setSSLPinning(true) // Optional: enable SSL pinning for API calls (default false).
                .setEnvironment(if (BuildConfig.DEBUG) Options.ENV_DEV else Options.ENV_PROD)
                .build()

            Sign3Intelligence.getInstance(this).initAsync(options) { success ->
                Log.i(TAG, "Sign3Intelligence init : $success")
            }
        }
    }

    private companion object {
        const val TAG = "Sign3App"
    }
}
