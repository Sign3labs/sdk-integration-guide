package com.android.sdk_integration_guide

import android.content.ClipData
import android.content.ClipboardManager
import android.util.Log
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sign3.public_classes.Sign3Intelligence

/** Shared toolbar wiring, SDK-readiness guard and the Session ID popup. */
abstract class Sign3BaseActivity : AppCompatActivity() {

    protected fun setupToolbar(showUp: Boolean) {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(showUp)

        // Edge-to-edge: light icons over the blue app bar, and keep scrolling
        // content clear of the navigation bar.
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        findViewById<View?>(R.id.content_scroll)?.let { scroll ->
            val basePadding = scroll.paddingBottom
            ViewCompat.setOnApplyWindowInsetsListener(scroll) { v, insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, basePadding + bars.bottom)
                insets
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * Runs [block] only when the SDK finished initialising. Calling SDK APIs before
     * a successful init is unsupported, so the demo surfaces the state instead.
     */
    protected inline fun withSdk(block: () -> Unit): Boolean {
        val message = when (App.sdkState) {
            App.SdkState.READY -> null
            App.SdkState.INITIALISING -> getString(R.string.sdk_initialising)
            App.SdkState.MISSING_CREDENTIALS -> getString(R.string.sdk_missing_credentials)
            App.SdkState.FAILED -> getString(R.string.sdk_init_failed)
        }
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            return false
        }
        block()
        return true
    }

    /** Sign3Intelligence.getSessionId() shown in a popup with a copy action. */
    protected fun showSessionIdDialog() {
        val sessionId = if (App.sdkState == App.SdkState.READY) {
            // getSessionId: the current session identifier to correlate with backend calls.
            runCatching { Sign3Intelligence.getInstance(this).getSessionId() }
                .onFailure { Log.e("Sign3", "getSessionId failed", it) }
                .getOrNull()
        } else {
            null
        }
        val message = if (sessionId.isNullOrBlank()) getString(R.string.session_id_empty) else sessionId

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.session_id_dialog_title)
            .setMessage(message)
            .setPositiveButton(R.string.copy) { _, _ ->
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Sign3 session id", message))
                Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }
}
