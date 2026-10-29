package com.android.sdk_integration_guide

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.sdk_integration_guide.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * One screen that exercises every Sign3 feature through [Sign3Manager].
 * Results are written to Logcat (tag "Sign3Manager") and mirrored in the log view below the buttons.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var captureId: String? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            Log.d(TAG, "Optional permissions: $granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.fetchIntelligence.setOnClickListener {
            log("getIntelligence() ...")
            Sign3Manager.getIntelligence(this) { result -> log(result) }
        }
        binding.updateOptions.setOnClickListener {
            Sign3Manager.updateOptions(
                this,
                userId = binding.userId.text?.toString(),
                phoneNumber = binding.phoneNumber.text?.toString(),
            )
            log("updateOptions() applied; it is sent with the next getIntelligence()")
        }
        binding.sessionId.setOnClickListener {
            log("getSessionId() = ${Sign3Manager.getSessionId(this)}")
        }
        binding.setCaptureContext.setOnClickListener {
            val name = binding.captureContext.text?.toString()?.trim()
            Sign3Manager.setCaptureContext(this, name, attributes = mapOf("flow" to "sample"))
            log(if (name.isNullOrBlank()) "setCaptureContext() cleared" else "setCaptureContext(\"$name\", attributes)")
        }
        binding.startCapture.setOnClickListener {
            captureId = Sign3Manager.startBehaviourCapture(this)
            log("startAnalyzingBehaviour() captureId=$captureId. Type and tap around, then stop.")
        }
        binding.stopCapture.setOnClickListener {
            Sign3Manager.stopBehaviourCapture(this)
            captureId = null
            log("stopAnalyzingBehaviour()")
        }
        binding.protectScreen.setOnClickListener {
            Sign3Manager.protectScreen(this)
            log("protectScreenCapture(activity): screenshots blocked")
        }
        binding.unprotectScreen.setOnClickListener {
            Sign3Manager.unprotectScreen(this)
            log("stopScreenCaptureProtection(activity)")
        }
        binding.protectedDialog.setOnClickListener { showProtectedDialog() }
    }

    private fun showProtectedDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.protected_dialog_title)
            .setMessage(R.string.protected_dialog_message)
            .setPositiveButton(R.string.close, null)
            .create()
        dialog.setOnDismissListener { Sign3Manager.unprotectDialog(this, dialog) }
        Sign3Manager.protectDialog(this, dialog)
        dialog.show()
    }

    private fun log(message: String) {
        Log.i(TAG, message)
        binding.log.append("> $message\n\n")
    }

    override fun onDestroy() {
        // The SDK stops on app kill, but stopping an open capture explicitly is recommended.
        if (captureId != null) Sign3Manager.stopBehaviourCapture(this)
        super.onDestroy()
    }

    private companion object {
        const val TAG = "Sign3Sample"
    }
}
