package com.android.sdk_integration_guide

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.sign3.public_classes.Sign3Intelligence

/**
 * Sign3Intelligence.setCaptureContext() / setCaptureContext(name) /
 * setCaptureContext(name, attributes), startAnalyzingBehaviour(), stopAnalyzingBehaviour()
 */
class BehaviouralActivity : Sign3BaseActivity() {

    private lateinit var contextName: TextInputEditText
    private lateinit var contextAttributes: TextInputEditText
    private lateinit var captureStatus: TextView
    private lateinit var log: TextView
    private lateinit var startButton: MaterialButton
    private lateinit var stopButton: MaterialButton

    private var captureId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_behavioural)
        setupToolbar(showUp = true)

        contextName = findViewById(R.id.context_name)
        contextAttributes = findViewById(R.id.context_attributes)
        captureStatus = findViewById(R.id.capture_status)
        log = findViewById(R.id.log)
        startButton = findViewById(R.id.start_capture)
        stopButton = findViewById(R.id.stop_capture)

        findViewById<MaterialButton>(R.id.set_capture_context).setOnClickListener { setCaptureContext() }
        startButton.setOnClickListener { startCapture() }
        stopButton.setOnClickListener { stopCapture() }
        findViewById<MaterialButton>(R.id.playground_button).setOnClickListener {
            Toast.makeText(this, R.string.playground_button, Toast.LENGTH_SHORT).show()
        }
        updateButtons()
    }

    /** Picks the overload that matches the inputs so all three variants are exercised. */
    private fun setCaptureContext() {
        withSdk {
            val name = contextName.text?.toString()?.trim().orEmpty()
            val attrs = parseKeyValues(contextAttributes.text?.toString().orEmpty())

            val message = when {
                name.isEmpty() -> {
                    // setCaptureContext(): clear any previously set context.
                    Sign3Intelligence.getInstance(this).setCaptureContext()
                    getString(R.string.capture_context_set_none)
                }
                attrs.isEmpty() -> {
                    // setCaptureContext(name): label the screen/flow being captured.
                    Sign3Intelligence.getInstance(this).setCaptureContext(name)
                    getString(R.string.capture_context_set_name, name)
                }
                else -> {
                    // setCaptureContext(name, attrs): label plus extra key/value metadata.
                    Sign3Intelligence.getInstance(this).setCaptureContext(name, attrs)
                    getString(R.string.capture_context_set_full, name, attrs.size)
                }
            }
            appendLog(message)
        }
    }

    private fun startCapture() {
        withSdk {
            // startAnalyzingBehaviour: begin capturing touch/typing behaviour; returns a captureId.
            val id = Sign3Intelligence.getInstance(this).startAnalyzingBehaviour()
            captureId = id
            captureStatus.text = getString(R.string.capture_status_running, id)
            appendLog("startAnalyzingBehaviour() → captureId=$id")
            updateButtons()
        }
    }

    private fun stopCapture() {
        withSdk {
            // stopAnalyzingBehaviour: end the capture and flush the collected signals.
            Sign3Intelligence.getInstance(this).stopAnalyzingBehaviour()
            captureId = null
            captureStatus.setText(R.string.capture_status_idle)
            appendLog(getString(R.string.capture_stopped))
            updateButtons()
        }
    }

    private fun updateButtons() {
        val running = captureId != null
        startButton.isEnabled = !running
        stopButton.isEnabled = running
    }

    private fun appendLog(message: String) {
        Log.i(TAG, message)
        log.append(message + "\n")
    }

    override fun onDestroy() {
        // The SDK stops on app kill, but stopping explicitly is recommended.
        if (captureId != null) Sign3Intelligence.getInstance(this).stopAnalyzingBehaviour()
        super.onDestroy()
    }

    private companion object {
        const val TAG = "Sign3Behavioural"
    }
}
