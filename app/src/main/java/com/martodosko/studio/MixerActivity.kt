// ==================================================
// FILE: MixerActivity.kt — TUGMA SA fragment_mixer.xml ✅
// VERSION: 1.0.62 — HAKBANG 1: TUGMA SA KASALUKUYANG XML
// UPDATED: 2026-09-13
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_mixer) // ✅ TAMA — fragment_mixer.xml

        Toast.makeText(this, "🎚️ Mixer Loaded — Hakbang 1", Toast.LENGTH_SHORT).show()

        // ==============================================
        // ✅ KALIWA — MGA PIHITAN (NASA XML NA!)
        // ==============================================
        setupKnob(R.id.knob_gain, R.id.val_gain, -12, 12, "dB")
        setupKnob(R.id.knob_bass, R.id.val_bass, -12, 12, "dB")
        setupKnob(R.id.knob_treble, R.id.val_treble, -12, 12, "dB")

        // ==============================================
        // ✅ KANAN — SLIDERS (NASA XML NA!)
        // ==============================================
        val sliderLeft = findViewById<SeekBar>(R.id.slider_left)
        val sliderRight = findViewById<SeekBar>(R.id.slider_right)
        val sliderMaster = findViewById<SeekBar>(R.id.slider_master)
        val masterVal = findViewById<TextView>(R.id.master_vol_val)

        // Left Slider
        sliderLeft.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                val pos = ((p - 50) / 5f).roundToInt()
                val text = when {
                    pos < -1 -> "L ${pos * -1}"
                    pos > 1 -> "R $pos"
                    else -> "C"
                }
                findViewById<TextView>(R.id.slider_left)?.contentDescription = text
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Right Slider
        sliderRight.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                val pos = ((p - 50) / 5f).roundToInt()
                val text = when {
                    pos < -1 -> "L ${pos * -1}"
                    pos > 1 -> "R $pos"
                    else -> "C"
                }
                findViewById<TextView>(R.id.slider_right)?.contentDescription = text
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Master Volume Slider
        sliderMaster.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                val db = ((p - 65) * 0.3).roundToInt()
                masterVal.text = if (db >= 0) "+$db dB" else "$db dB"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    // ==============================================
    // ✅ PIHITAN CONTROL — HILA PATAAS/PABABA
    // ==============================================
    private fun setupKnob(knobId: Int, valueId: Int, min: Int, max: Int, unit: String) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        val range = max - min
        var currentValue = (min + max) / 2

        fun updateValue() {
            valueText.text = when {
                unit == "dB" && currentValue >= 0 -> "+$currentValue dB"
                unit.isNotEmpty() -> "$currentValue $unit"
                else -> "$currentValue"
            }
            // Paikutin ang knob (-135° hanggang +135°)
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
}
