package com.android.sdk_integration_guide

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sign3.public_classes.Sign3Intelligence

/**
 * Sign3Intelligence.protectScreenCapture(Activity | Dialog) and
 * stopScreenCaptureProtection(Activity | Dialog)
 */
class ScreenCaptureActivity : Sign3BaseActivity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_capture)
        setupToolbar(showUp = true)

        status = findViewById(R.id.protection_status)

        findViewById<MaterialButton>(R.id.protect_activity).setOnClickListener {
            withSdk {
                // protectScreenCapture(Activity): block screenshots and screen recording.
                Sign3Intelligence.getInstance(this).protectScreenCapture(this)
                status.setText(R.string.protection_status_on)
            }
        }
        findViewById<MaterialButton>(R.id.unprotect_activity).setOnClickListener {
            withSdk {
                // stopScreenCaptureProtection(Activity): allow screenshots again.
                Sign3Intelligence.getInstance(this).stopScreenCaptureProtection(this)
                status.setText(R.string.protection_status_off)
            }
        }
        findViewById<MaterialButton>(R.id.show_protected_dialog).setOnClickListener { showProtectedDialog() }
    }

    private fun showProtectedDialog() {
        withSdk {
            val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(R.string.protected_dialog_title)
                .setMessage(R.string.protected_dialog_message)
                .setPositiveButton(R.string.close, null)
                .create()
            // Dialog variants: protect before show, release on dismiss.
            dialog.setOnDismissListener { Sign3Intelligence.getInstance(this).stopScreenCaptureProtection(dialog) }
            Sign3Intelligence.getInstance(this).protectScreenCapture(dialog)
            dialog.show()
        }
    }
}
