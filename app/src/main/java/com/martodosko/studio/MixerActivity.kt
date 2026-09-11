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
        setContentView(R.layout.fragment_mixer)

        // ==============================================
        // ✅ CHANNEL 1 — 🎤 VOCALS / MIC — 7 KNOBS, WALANG SLIDER!
        // ==============================================
        setupKnobControl(R.id.voc_treble, R.id.voc_treble_val, -12, 12, "dB")
        setupKnobControl(R.id.voc_mid, R.id.voc_mid_val, -12, 12, "dB")
        setupKnobControl(R.id.voc_bass, R.id.voc_bass_val, -12, 12, "dB")
        setupKnobControl(R.id.voc_reverb, R.id.voc_reverb_val, 0, 100, "%")
        setupKnobControl(R.id.voc_delay, R.id.voc_delay_val, 0, 800, "ms")
        setupKnobControl(R.id.voc_decay, R.id.voc_decay_val, 0, 100, "%")
        setupKnobControl(R.id.voc_vol, R.id.voc_vol_val, -48, 12, "dB")

        // ✅ CH 1 — Mute
        findViewById<ToggleButton>(R.id.voc_mute)?.setOnCheckedChangeListener { _, isChecked ->
            setChannelAlpha("voc_", if (isChecked) 0.3f else 1.0f)
        }

        // ==============================================
        // ✅ CHANNEL 2 — 🎸 INSTRUMENTS — 7 KNOBS, WALANG SLIDER!
        // ==============================================
        setupKnobControl(R.id.inst_treble, R.id.inst_treble_val, -12, 12, "dB")
        setupKnobControl(R.id.inst_mid, R.id.inst_mid_val, -12, 12, "dB")
        setupKnobControl(R.id.inst_bass, R.id.inst_bass_val, -12, 12, "dB")
        setupKnobControl(R.id.inst_reverb, R.id.inst_reverb_val, 0, 100, "%")
        setupKnobControl(R.id.inst_delay, R.id.inst_delay_val, 0, 800, "ms")
        setupKnobControl(R.id.inst_decay, R.id.inst_decay_val, 0, 100, "%")
        setupKnobControl(R.id.inst_vol, R.id.inst_vol_val, -48, 12, "dB")

        // ✅ CH 2 — Mute
        findViewById<ToggleButton>(R.id.inst_mute)?.setOnCheckedChangeListener { _, isChecked ->
            setChannelAlpha("inst_", if (isChecked) 0.3f else 1.0f)
        }

        // ==============================================
        // ✅ CHANNEL 3 — 🎵 MUSIC / BACKGROUND — 7 KNOBS, WALANG SLIDER!
        // ==============================================
        setupKnobControl(R.id.mus_treble, R.id.mus_treble_val, -12, 12, "dB")
        setupKnobControl(R.id.mus_mid, R.id.mus_mid_val, -12, 12, "dB")
        setupKnobControl(R.id.mus_bass, R.id.mus_bass_val, -12, 12, "dB")
        setupKnobControl(R.id.mus_reverb, R.id.mus_reverb_val, 0, 100, "%")
        setupKnobControl(R.id.mus_delay, R.id.mus_delay_val, 0, 800, "ms")
        setupKnobControl(R.id.mus_decay, R.id.mus_decay_val, 0, 100, "%")
        setupKnobControl(R.id.mus_vol, R.id.mus_vol_val, -48, 12, "dB")

        // ✅ CH 3 — Mute
        findViewById<ToggleButton>(R.id.mus_mute)?.setOnCheckedChangeListener { _, isChecked ->
            setChannelAlpha("mus_", if (isChecked) 0.3f else 1.0f)
        }

        // ==============================================
        // ✅ KANAN — STEREO SLIDERS — LEFT / RIGHT / MASTER — ITO LANG MAY SLIDER!
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

        // ✅ MASTER VOLUME SLIDER — PINAKAHULI SA KANAN!
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

        // ==============================================
        // ✅ MUTE ALL + BYPASS
        // ==============================================
        findViewById<ToggleButton>(R.id.btn_mute_all)?.setOnCheckedChangeListener { _, isChecked ->
            val alpha = if (isChecked) 0.3f else 1.0f
            setChannelAlpha("voc_", alpha)
            setChannelAlpha("inst_", alpha)
            setChannelAlpha("mus_", alpha)
        }

        findViewById<ToggleButton>(R.id.btn_bypass)?.setOnCheckedChangeListener { button, isChecked ->
            button?.setBackgroundColor(if (isChecked) 0xFF40E0D0.toInt() else 0xFF2A2A3C.toInt())
        }
    }

    // ==============================================
    // ✅ KNOB TOUCH CONTROL — HAWAKIN AT I-UP/DOWN PARA MAGBAGO!
    // ==============================================
    private fun setupKnobControl(knobId: Int, valueId: Int, min: Int, max: Int, unit: String) {
        val knob = findViewById<View>(knobId)
        val valueText = findViewById<TextView>(valueId)
        val range = max - min
        var currentValue = (min + max) / 2

        fun updateValue() {
            valueText.text = when (unit) {
                "dB" -> if (currentValue >= 0) "+$currentValue dB" else "$currentValue dB"
                else -> "$currentValue $unit"
            }
        }

        updateValue()

        knob.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                val y = event.y
                val height = knob.height.toFloat()
                val percent = 1f - (y / height).coerceIn(0f, 1f)
                currentValue = (min + percent * range).roundToInt()
                updateValue()
            }
            true
        }
    }

    // ==============================================
    // ✅ HELPER — MUTE EFFECT SA BUONG CHANNEL
    // ==============================================
    private fun setChannelAlpha(prefix: String, alpha: Float) {
        val ids = listOf("treble", "mid", "bass", "reverb", "delay", "decay", "vol")
        ids.forEach { idName ->
            val resId = resources.getIdentifier("${prefix}$idName", "id", packageName)
            if (resId != 0) findViewById<View>(resId).alpha = alpha
        }
    }
}
