// ==================================================
// FILE: MixerActivity.kt — ISANG CHANNEL LANG ✅
// VERSION: 1.0.62 — PINAG-ISAHAN
// UPDATED: 2026-09-12
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer) // ✅ SIGURADO: activity_mixer.xml

        // ==============================================
        // ✅ ISANG CHANNEL LANG — 🎤 VOCALS / MIC — 11 KNOBS!
        // ==============================================
        setupKnobControl(R.id.knob_gain, R.id.val_gain, -12, 12, "dB")
        setupKnobControl(R.id.knob_bass, R.id.val_bass, -12, 12, "dB")
        setupKnobControl(R.id.knob_lowmid, R.id.val_lowmid, -12, 12, "dB")
        setupKnobControl(R.id.knob_mid, R.id.val_mid, -12, 12, "dB")
        setupKnobControl(R.id.knob_highmid, R.id.val_highmid, -12, 12, "dB")
        setupKnobControl(R.id.knob_treble, R.id.val_treble, -12, 12, "dB")
        setupKnobControl(R.id.knob_presence, R.id.val_presence, -12, 12, "dB")
        setupKnobControl(R.id.knob_reverb, R.id.val_reverb, 0, 100, "%")
        setupKnobControl(R.id.knob_delay, R.id.val_delay, 0, 800, "ms")
        setupKnobControl(R.id.knob_compressor, R.id.val_compressor, 0, 100, "%")
        setupKnobControl(R.id.knob_pan, R.id.val_pan, -10, 10, "")
        setupKnobControl(R.id.knob_vol, R.id.val_vol, -48, 12, "dB")

        // ==============================================
        // ✅ KANAN — SLIDERS & PINDUTAN — NANDOON PA RIN!
        // ==============================================
        val sliderLeft = findViewById<SeekBar>(R.id.slider_left)
        val sliderRight = findViewById<SeekBar>(R.id.slider_right)
        val btnMono = findViewById<ToggleButton>(R.id.btn_mono)

        btnMono.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sliderLeft.progress = 50
                sliderRight.progress = 50
                sliderLeft.isEnabled = false
                sliderRight.isEnabled = false
            } else {
                sliderLeft.isEnabled = true
                sliderRight.isEnabled = true
            }
        }

        // ✅ MASTER VOLUME SLIDER
        val sliderMaster = findViewById<SeekBar>(R.id.slider_master)
        val masterVolVal = findViewById<TextView>(R.id.master_vol_val)
        sliderMaster.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                val db = ((p - 65) * 0.3).roundToInt()
                masterVolVal.text = if (db >= 0) "+$db dB" else "$db dB"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // ✅ MUTE ALL + BYPASS
        findViewById<ToggleButton>(R.id.btn_mute_all)?.setOnCheckedChangeListener { _, isChecked ->
            findViewById<View>(R.id.knob_vol).alpha = if (isChecked) 0.3f else 1.0f
        }

        findViewById<ToggleButton>(R.id.btn_bypass)?.setOnCheckedChangeListener { button, isChecked ->
            button?.setBackgroundColor(if (isChecked) 0xFF40E0D0.toInt() else 0xFF2A2A3C.toInt())
        }
    }

    // ==============================================
    // ✅ KNOB TOUCH CONTROL — HAWAKIN AT I-UP/DOWN
    // ==============================================
    private fun setupKnobControl(knobId: Int, valueId: Int, min: Int, max: Int, unit: String) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        val range = max - min
        var currentValue = (min + max) / 2

        fun updateValue() {
            valueText.text = when {
                unit == "dB" && currentValue >= 0 -> "+$currentValue dB"
                unit.isEmpty() -> when {
                    currentValue < -3 -> "LEFT ${currentValue * -1}"
                    currentValue > 3 -> "RIGHT $currentValue"
                    else -> "CENTER"
                }
                unit.isNotEmpty() -> "$currentValue $unit"
                else -> "$currentValue"
            }
        }

        updateValue()

        knob.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val percent = (1f - (event.y / knob.height).coerceIn(0f, 1f))
                currentValue = (min + percent * range).roundToInt()
                updateValue()
            }
            true
        }
    }
}
