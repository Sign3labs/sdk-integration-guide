package com.android.sdk_integration_guide

import android.app.Application
import android.os.Build
import android.util.Log
import com.sign3.public_classes.Sign3Intelligence
import com.sign3.public_classes.common.Options

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initSign3()
    }

    private fun initSign3() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (BuildConfig.SIGN3_CLIENT_ID.isBlank() || BuildConfig.SIGN3_CLIENT_SECRET.isBlank()) {
                Log.w(TAG, "Sign3 credentials missing: set SIGN3_CLIENT_ID and SIGN3_CLIENT_SECRET in credentials.properties")
                sdkState = SdkState.MISSING_CREDENTIALS
                return
            }

            val options = Options.Builder()
                .setClientId(BuildConfig.SIGN3_CLIENT_ID)
                .setClientSecret(BuildConfig.SIGN3_CLIENT_SECRET)
                .setSSLPinning(true) // Optional: If you want SSL pinning in API calls, default value is false.
                .setEnvironment(if (BuildConfig.DEBUG) Options.ENV_DEV else Options.ENV_PROD) // For Prod: Options.ENV_PROD, For Dev: Options.ENV_DEV
                .build()

            sdkState = SdkState.INITIALISING
            Sign3Intelligence.getInstance(this).initAsync(options) {
                // to check if the SDK is initialized correctly or not
                Log.i(TAG, "Sign3Intelligence init : $it")
                sdkState = if (it) SdkState.READY else SdkState.FAILED
            }
        }
    }

    enum class SdkState { MISSING_CREDENTIALS, INITIALISING, READY, FAILED }

    companion object {
        private const val TAG = "Sign3App"

        /** Current Sign3 SDK state; screens check this before calling SDK APIs. */
        @Volatile
        var sdkState: SdkState = SdkState.INITIALISING
            private set
    }
}
