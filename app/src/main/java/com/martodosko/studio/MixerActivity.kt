// ==================================================
// FILE: MixerActivity.kt — TUGMA SA fragment_mixer.xml ✅
// VERSION: 1.0.64 — HORIZONTAL SLIDERS + 12 KNOBS + 4 BUTTONS
// UPDATED: 2026-09-13
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import android.widget.Toast
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_mixer) // ✅ TAMA — tugma sa file

        Toast.makeText(this, "🎚️ Mixer Loaded — Horizontal Sliders", Toast.LENGTH_SHORT).show()

        // ==============================================
        // ✅ KALIWA — 12 PIHITAN (Knobs)
        // ==============================================
        // EQ Section
        setupKnob(R.id.knob_gain, R.id.val_gain, -12, 12, "dB")
        setupKnob(R.id.knob_bass, R.id.val_bass, -12, 12, "dB")
        setupKnob(R.id.knob_lowmid, R.id.val_lowmid, -12, 12, "dB")
        setupKnob(R.id.knob_mid, R.id.val_mid, -12, 12, "dB")
        setupKnob(R.id.knob_highmid, R.id.val_highmid, -12, 12, "dB")
        setupKnob(R.id.knob_treble, R.id.val_treble, -12, 12, "dB")
        setupKnob(R.id.knob_presence, R.id.val_presence, -12, 12, "dB")

        // Effects Section
        setupKnobPercent(R.id.knob_reverb, R.id.val_reverb)
        setupKnobDelay(R.id.knob_delay, R.id.val_delay)
        setupKnobPercent(R.id.knob_compressor, R.id.val_compressor)
        setupKnobPan(R.id.knob_pan, R.id.val_pan)

        // Volume Knob
        setupKnobVolume(R.id.knob_vol, R.id.val_vol)

        // ==============================================
        // ✅ KANAN — HORIZONTAL SLIDERS (Left / Right / Master)
        // ==============================================
        val sliderLeft = findViewById<SeekBar>(R.id.slider_left)
        val sliderRight = findViewById<SeekBar>(R.id.slider_right)
        val sliderMaster = findViewById<SeekBar>(R.id.slider_master)
        val masterVal = findViewById<TextView>(R.id.master_vol_val)

        // LEFT Slider — Horizontal
        sliderLeft.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val db = ((progress - 50) * 0.5).roundToInt()
                sb?.contentDescription = if (db >= 0) "+$db dB" else "$db dB"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // RIGHT Slider — Horizontal
        sliderRight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val db = ((progress - 50) * 0.5).roundToInt()
                sb?.contentDescription = if (db >= 0) "+$db dB" else "$db dB"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // MASTER Slider — Horizontal
        sliderMaster.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                val db = ((progress - 65) * 0.4).roundToInt()
                masterVal.text = if (db >= 0) "+$db dB" else "$db dB"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // ==============================================
        // ✅ MGA PINDUTAN — Mute / Mono / Mute All / Bypass
        // ==============================================
        // Channel Mute
        findViewById<ToggleButton>(R.id.btn_mute).setOnCheckedChangeListener { _, isChecked ->
            findViewById<View>(R.id.knob_vol).alpha = if (isChecked) 0.3f else 1.0f
        }

        // Mono / Stereo
        findViewById<ToggleButton>(R.id.btn_mono).setOnCheckedChangeListener { _, isChecked ->
            sliderLeft.alpha = if (isChecked) 0.5f else 1.0f
            sliderRight.alpha = if (isChecked) 0.5f else 1.0f
        }

        // Mute All
        findViewById<ToggleButton>(R.id.btn_mute_all).setOnCheckedChangeListener { _, isChecked ->
            sliderMaster.alpha = if (isChecked) 0.3f else 1.0f
        }

        // Bypass
        findViewById<ToggleButton>(R.id.btn_bypass).setOnCheckedChangeListener { _, isChecked ->
            window.decorView.rootView.setBackgroundColor(
                if (isChecked) 0xFF1A2E1A.toInt() else 0xFF080810.toInt()
            )
        }
    }

    // ==============================================
    // ✅ PIHITAN — dB Values (-12 hanggang +12)
    // ==============================================
    private fun setupKnob(knobId: Int, valueId: Int, min: Int, max: Int, unit: String) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        val range = max - min
        var currentValue = 0 // Center = 0 dB

        fun updateValue() {
            valueText.text = when {
                currentValue >= 0 -> "+$currentValue $unit"
                else -> "$currentValue $unit"
            }
            knob.rotation = ((currentValue - min) / range.toFloat() * 270f) - 135f
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

    // ==============================================
    // ✅ PIHITAN — Percent (0% hanggang 100%)
    // ==============================================
    private fun setupKnobPercent(knobId: Int, valueId: Int) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        var percent = 0

        fun updateValue() {
            valueText.text = "$percent%"
            knob.rotation = (percent * 2.7f) - 135f
        }

        updateValue()

        knob.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                percent = ((1f - (event.y / knob.height).coerceIn(0f, 1f)) * 100).roundToInt()
                updateValue()
            }
            true
        }
    }

    // ==============================================
    // ✅ PIHITAN — Delay (0ms hanggang 800ms)
    // ==============================================
    private fun setupKnobDelay(knobId: Int, valueId: Int) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        var ms = 0

        fun updateValue() {
            valueText.text = "${ms}ms"
            knob.rotation = (ms / 800f * 270f) - 135f
        }

        updateValue()

        knob.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                ms = ((1f - (event.y / knob.height).coerceIn(0f, 1f)) * 800).roundToInt()
                updateValue()
            }
            true
        }
    }

    // ==============================================
    // ✅ PIHITAN — Pan (Left ← Center → Right)
    // ==============================================
    private fun setupKnobPan(knobId: Int, valueId: Int) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        var pos = 0 // -10 = Left, 0 = Center, +10 = Right

        fun updateValue() {
            valueText.text = when {
                pos < 0 -> "L ${pos * -1}"
                pos > 0 -> "R $pos"
                else -> "CENTER"
            }
            knob.rotation = (pos * 13.5f) // -135° hanggang +135°
        }

        updateValue()

        knob.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val percent = (1f - (event.y / knob.height).coerceIn(0f, 1f))
                pos = ((percent * 20) - 10).roundToInt()
                updateValue()
            }
            true
        }
    }

    // ==============================================
    // ✅ PIHITAN — Volume (-48dB hanggang +12dB)
    // ==============================================
    private fun setupKnobVolume(knobId: Int, valueId: Int) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        var db = 0

        fun updateValue() {
            valueText.text = if (db >= 0) "+$db dB" else "$db dB"
            knob.rotation = ((db + 48) / 60f * 270f) - 135f
        }

        updateValue()

        knob.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val percent = (1f - (event.y / knob.height).coerceIn(0f, 1f))
                db = ((percent * 60) - 48).roundToInt()
                updateValue()
            }
            true
        }
    }
}
