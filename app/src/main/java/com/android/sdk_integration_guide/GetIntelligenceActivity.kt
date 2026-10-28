package com.android.sdk_integration_guide

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.sign3.public_classes.common.IntelligenceError
import com.sign3.public_classes.common.IntelligenceListener
import com.sign3.public_classes.common.IntelligenceResponse
import java.util.Locale
import com.sign3.public_classes.Sign3Intelligence

/**
 * Landing screen: Sign3Intelligence.getIntelligence(IntelligenceListener).
 * The toolbar menu routes to the other Sign3Intelligence features and
 * shows getSessionId() in a popup.
 */
class GetIntelligenceActivity : Sign3BaseActivity() {

    private lateinit var fetchButton: MaterialButton
    private lateinit var progress: LinearProgressIndicator
    private lateinit var emptyState: TextView
    private lateinit var resultContainer: View
    private lateinit var sessionRisk: TextView
    private lateinit var deviceRisk: TextView
    private lateinit var ruleAction: TextView
    private lateinit var signals: LinearLayout
    private lateinit var result: TextView
    private lateinit var errorCard: MaterialCardView
    private lateinit var errorText: TextView

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            Log.d(TAG, "Optional permissions: $granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_intelligence)
        setupToolbar(showUp = false)

        fetchButton = findViewById(R.id.fetch_intelligence)
        progress = findViewById(R.id.progress)
        emptyState = findViewById(R.id.empty_state)
        resultContainer = findViewById(R.id.result_container)
        sessionRisk = findViewById(R.id.session_risk)
        deviceRisk = findViewById(R.id.device_risk)
        ruleAction = findViewById(R.id.rule_action)
        signals = findViewById(R.id.signals)
        result = findViewById(R.id.result)
        errorCard = findViewById(R.id.error_card)
        errorText = findViewById(R.id.error_text)

        fetchButton.setOnClickListener { fetchIntelligence() }

        // Optional permissions improve signal accuracy (location, sim affinity).
        if (savedInstanceState == null) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sign3, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update_options -> startActivity(Intent(this, UpdateOptionsActivity::class.java))
            R.id.action_behavioural -> startActivity(Intent(this, BehaviouralActivity::class.java))
            R.id.action_screen_capture -> startActivity(Intent(this, ScreenCaptureActivity::class.java))
            R.id.action_session_id -> showSessionIdDialog()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun fetchIntelligence() {
        withSdk {
            setLoading(true)
            errorCard.visibility = View.GONE

            // getIntelligence: fetch the device/session risk result; listener gets success or error.
            Sign3Intelligence.getInstance(this).getIntelligence(object : IntelligenceListener {
                override fun onSuccess(response: IntelligenceResponse) {
                    // Do something with the response
                    Log.i(TAG, "getIntelligence success: $response")
                    runOnUiThread {
                        setLoading(false)
                        render(response)
                    }
                }

                override fun onError(error: IntelligenceError) {
                    // Something went wrong, handle the error message
                    Log.e(TAG, "getIntelligence error: $error")
                    runOnUiThread {
                        setLoading(false)
                        emptyState.visibility = View.GONE
                        resultContainer.visibility = View.GONE
                        errorText.text = IntelligenceFormatter.format(error)
                        errorCard.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    private fun render(r: IntelligenceResponse) {
        emptyState.visibility = View.GONE
        resultContainer.visibility = View.VISIBLE

        sessionRisk.text = formatScore(r.sessionRiskScore)
        deviceRisk.text = formatScore(r.deviceRiskScore)

        val action = r.ruleAction
        if (action != null) {
            ruleAction.text = getString(R.string.rule_action_format, action.toString())
            ruleAction.visibility = View.VISIBLE
        } else {
            ruleAction.visibility = View.GONE
        }

        signals.removeAllViews()
        val inflater = LayoutInflater.from(this)
        listOf(
            "Rooted" to r.rooted,
            "Emulator" to r.emulator,
            "Cloned app" to r.cloned,
            "Hooking" to r.hooking,
            "App tampering" to r.appTampering,
            "VPN" to r.vpn,
            "Proxy" to r.proxy,
            "Geo spoofed" to r.geoSpoofed,
            "Unsecured Wi-Fi" to r.unsecuredWifi,
            "Remote app providers" to r.remoteAppProviders,
            "Mirrored screen" to r.mirroredScreen,
            "Factory reset" to r.factoryReset,
            "Developer options" to r.developerOptionsEnabled,
            "USB debugging" to r.usbDebugging,
            "Wireless debugging" to r.wirelessDebugging,
            "Harmful app detected" to r.harmfulAppDetected,
            "Keylogger detected" to r.keyloggerDetected,
            "Blacklisted device" to r.blacklistedDevice,
            "New device" to r.newDevice,
        ).forEach { (name, value) -> signals.addView(signalRow(inflater, name, value, riskWhenTrue = true)) }
        signals.addView(signalRow(inflater, "Genuine install", r.genuineInstall, riskWhenTrue = false))

        result.text = IntelligenceFormatter.format(r)
    }

    private fun signalRow(inflater: LayoutInflater, name: String, value: Boolean?, riskWhenTrue: Boolean): View {
        val row = inflater.inflate(R.layout.item_signal, signals, false)
        row.findViewById<TextView>(R.id.name).text = name
        row.findViewById<TextView>(R.id.value).text = value?.toString() ?: "—"

        val color = when {
            value == null -> R.color.signal_unknown
            value == riskWhenTrue -> R.color.signal_risk
            else -> R.color.signal_safe
        }
        val dot = row.findViewById<View>(R.id.dot)
        val drawable = DrawableCompat.wrap(dot.background.mutate())
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, color))
        dot.background = drawable
        return row
    }

    private fun formatScore(score: Float?): String =
        if (score == null) "—" else String.format(Locale.US, "%.1f", score)

    private fun setLoading(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        fetchButton.isEnabled = !loading
    }

    private companion object {
        const val TAG = "Sign3GetIntelligence"
    }
}
