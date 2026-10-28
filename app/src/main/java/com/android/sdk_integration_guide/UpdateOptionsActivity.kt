package com.android.sdk_integration_guide

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.sign3.public_classes.common.IntelligenceError
import com.sign3.public_classes.common.IntelligenceListener
import com.sign3.public_classes.common.IntelligenceResponse
import com.sign3.public_classes.common.OtpInputType
import com.sign3.public_classes.common.PhoneInputType
import com.sign3.public_classes.common.UpdateOptions
import com.sign3.public_classes.common.UserEventType
import com.sign3.public_classes.Sign3Intelligence

/** Sign3Intelligence.updateOptions(UpdateOptions) */
class UpdateOptionsActivity : Sign3BaseActivity() {

    private lateinit var userId: TextInputEditText
    private lateinit var phoneNumber: TextInputEditText
    private lateinit var merchantId: TextInputEditText
    private lateinit var phoneInputType: MaterialAutoCompleteTextView
    private lateinit var otpInputType: MaterialAutoCompleteTextView
    private lateinit var userEventType: MaterialAutoCompleteTextView
    private lateinit var attributes: TextInputEditText
    private lateinit var resultCard: MaterialCardView
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_options)
        setupToolbar(showUp = true)

        userId = findViewById(R.id.user_id)
        phoneNumber = findViewById(R.id.phone_number)
        merchantId = findViewById(R.id.merchant_id)
        phoneInputType = findViewById(R.id.phone_input_type)
        otpInputType = findViewById(R.id.otp_input_type)
        userEventType = findViewById(R.id.user_event_type)
        attributes = findViewById(R.id.attributes)
        resultCard = findViewById(R.id.result_card)
        resultView = findViewById(R.id.result)

        phoneInputType.bind(PhoneInputType.entries, PhoneInputType.MANUAL)
        otpInputType.bind(OtpInputType.entries, OtpInputType.MANUAL)
        userEventType.bind(UserEventType.entries, UserEventType.OTHERS)

        findViewById<MaterialButton>(R.id.update_options).setOnClickListener { applyOptions() }
        findViewById<MaterialButton>(R.id.update_and_fetch).setOnClickListener {
            if (applyOptions()) fetchIntelligence()
        }
    }

    private fun <T : Enum<T>> MaterialAutoCompleteTextView.bind(values: List<T>, default: T) {
        setSimpleItems(values.map { it.name }.toTypedArray())
        setText(default.name, false)
    }

    private inline fun <reified T : Enum<T>> MaterialAutoCompleteTextView.selected(): T =
        enumValueOf(text.toString())

    private fun buildUpdateOptions(): UpdateOptions {
        val attrs = parseKeyValues(attributes.text?.toString().orEmpty())
        // UpdateOptions: user/transaction context sent with the next getIntelligence().
        val builder = UpdateOptions.Builder()
            .setPhoneInputType(phoneInputType.selected<PhoneInputType>())
            .setOtpInputType(otpInputType.selected<OtpInputType>())
            .setUserEventType(userEventType.selected<UserEventType>())

        userId.trimmed()?.let { builder.setUserId(it) }
        phoneNumber.trimmed()?.let { builder.setPhoneNumber(it) }
        merchantId.trimmed()?.let { builder.setMerchantId(it) }
        if (attrs.isNotEmpty()) builder.setAdditionalAttributes(attrs)

        return builder.build()
    }

    private fun TextInputEditText.trimmed(): String? =
        text?.toString()?.trim()?.takeIf { it.isNotEmpty() }

    private fun applyOptions(): Boolean = withSdk {
        val updateOptions = buildUpdateOptions()
        // updateOptions: attach the context above to the SDK session.
        Sign3Intelligence.getInstance(this).updateOptions(updateOptions)
        Log.i(TAG, "updateOptions: $updateOptions")
        resultCard.visibility = View.VISIBLE
        resultView.text = getString(R.string.options_updated, describe())
    }

    private fun describe(): String = IntelligenceFormatter.format(
        linkedMapOf(
            "userId" to userId.trimmed(),
            "phoneNumber" to phoneNumber.trimmed(),
            "merchantId" to merchantId.trimmed(),
            "phoneInputType" to phoneInputType.text?.toString(),
            "otpInputType" to otpInputType.text?.toString(),
            "userEventType" to userEventType.text?.toString(),
            "additionalAttributes" to parseKeyValues(attributes.text?.toString().orEmpty()),
        )
    )

    /** Options only take effect once getIntelligence() is called. */
    private fun fetchIntelligence() {
        resultView.append("\n\n" + getString(R.string.fetching))
        // getIntelligence: fetch the device/session risk result asynchronously.
        Sign3Intelligence.getInstance(this).getIntelligence(object : IntelligenceListener {
            override fun onSuccess(response: IntelligenceResponse) {
                // Do something with the response
                runOnUiThread { resultView.text = IntelligenceFormatter.format(response) }
            }

            override fun onError(error: IntelligenceError) {
                // Something went wrong, handle the error message
                runOnUiThread { resultView.text = IntelligenceFormatter.format(error) }
            }
        })
    }

    private companion object {
        const val TAG = "Sign3UpdateOptions"
    }
}

/** Parses "KEY=VALUE" lines into a map; blank lines and lines without '=' are ignored. */
internal fun parseKeyValues(text: String): Map<String, String> =
    text.lineSequence()
        .map { it.trim() }
        .filter { it.contains('=') }
        .associate { line ->
            val idx = line.indexOf('=')
            line.substring(0, idx).trim() to line.substring(idx + 1).trim()
        }
        .filterKeys { it.isNotEmpty() }
