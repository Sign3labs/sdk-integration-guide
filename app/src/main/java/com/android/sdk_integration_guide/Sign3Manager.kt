package com.android.sdk_integration_guide

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.sign3.public_classes.Sign3Intelligence
import com.sign3.public_classes.common.IntelligenceError
import com.sign3.public_classes.common.IntelligenceListener
import com.sign3.public_classes.common.IntelligenceResponse
import com.sign3.public_classes.common.OtpInputType
import com.sign3.public_classes.common.PhoneInputType
import com.sign3.public_classes.common.UpdateOptions
import com.sign3.public_classes.common.UserEventType

/**
 * The single place in the app that talks to [Sign3Intelligence].
 *
 * Screens call these helpers instead of the SDK directly, so every SDK
 * interaction (and its logging) lives here and is easy to find and change.
 * Initialisation happens once in [App.onCreate].
 */
object Sign3Manager {

    private const val TAG = "Sign3Manager"

    /**
     * Fetches the device/session intelligence. [onResult] is delivered on the
     * main thread with a printable summary of the success or error payload.
     */
    fun getIntelligence(context: Context, onResult: (String) -> Unit) {
        val mainExecutor = ContextCompat.getMainExecutor(context)
        Sign3Intelligence.getInstance(context).getIntelligence(object : IntelligenceListener {
            override fun onSuccess(response: IntelligenceResponse) {
                // Use the response here, e.g. response.sessionRiskScore, response.rooted ...
                Log.i(TAG, "getIntelligence success: $response")
                mainExecutor.execute { onResult(response.toString()) }
            }

            override fun onError(error: IntelligenceError) {
                Log.e(TAG, "getIntelligence error: $error")
                mainExecutor.execute { onResult("error: $error") }
            }
        })
    }

    /**
     * Attaches user context to the next [getIntelligence] call. Options are
     * consumed by that call, so update them again before each fetch if needed.
     */
    fun updateOptions(context: Context, userId: String?, phoneNumber: String?) {
        val builder = UpdateOptions.Builder()
            .setPhoneInputType(PhoneInputType.MANUAL)
            .setOtpInputType(OtpInputType.MANUAL)
            .setUserEventType(UserEventType.LOGIN)
            .setMerchantId("sample-merchant")
            .setAdditionalAttributes(mapOf("SCREEN" to "main"))
        if (!userId.isNullOrBlank()) builder.setUserId(userId)
        if (!phoneNumber.isNullOrBlank()) builder.setPhoneNumber(phoneNumber)

        val options = builder.build()
        Sign3Intelligence.getInstance(context).updateOptions(options)
        Log.i(TAG, "updateOptions: $options")
    }

    /** Session identifier to correlate this device session with your backend calls. */
    fun getSessionId(context: Context): String =
        Sign3Intelligence.getInstance(context).getSessionId().also { Log.i(TAG, "getSessionId: $it") }

    /**
     * Labels the behavioural capture with the screen/flow name and optional attributes.
     * Call it before [startBehaviourCapture]. A blank [name] clears the context.
     */
    fun setCaptureContext(context: Context, name: String?, attributes: Map<String, String>? = null) {
        val sdk = Sign3Intelligence.getInstance(context)
        when {
            name.isNullOrBlank() -> sdk.setCaptureContext()               // clear previous context
            attributes.isNullOrEmpty() -> sdk.setCaptureContext(name)   // name only
            else -> sdk.setCaptureContext(name, attributes)             // name + attributes
        }
        Log.i(TAG, "setCaptureContext: name=$name attributes=$attributes")
    }

    /** Starts collecting touch/typing signals. Returns the captureId. */
    fun startBehaviourCapture(context: Context): String =
        Sign3Intelligence.getInstance(context).startAnalyzingBehaviour()
            .also { Log.i(TAG, "startAnalyzingBehaviour: captureId=$it") }

    /** Stops the capture and flushes the collected signals. */
    fun stopBehaviourCapture(context: Context) {
        Sign3Intelligence.getInstance(context).stopAnalyzingBehaviour()
        Log.i(TAG, "stopAnalyzingBehaviour")
    }

    /** Blocks screenshots and screen recording for [activity]. */
    fun protectScreen(activity: Activity) =
        Sign3Intelligence.getInstance(activity).protectScreenCapture(activity)

    fun unprotectScreen(activity: Activity) =
        Sign3Intelligence.getInstance(activity).stopScreenCaptureProtection(activity)

    /** Same as [protectScreen] but for a [Dialog]; call before showing it. */
    fun protectDialog(context: Context, dialog: Dialog) =
        Sign3Intelligence.getInstance(context).protectScreenCapture(dialog)

    fun unprotectDialog(context: Context, dialog: Dialog) =
        Sign3Intelligence.getInstance(context).stopScreenCaptureProtection(dialog)
}
