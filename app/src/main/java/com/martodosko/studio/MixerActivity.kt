package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import android.widget.ImageView
import android.view.animation.OvershootInterpolator
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_mixer)

        // ==============================================
        // ✅ CHANNEL 1 — VOCAL / MIC
        // ==============================================
        val ch1Volume = findViewById<SeekBar>(R.id.ch1_volume)
        val ch1VolumeText = findViewById<TextView>(R.id.ch1_volume_text)

        ch1Volume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val db = ((progress - 50) * 0.6).roundToInt()
                ch1VolumeText.text = if (db >= 0) "+$db dB" else "$db dB"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // ✅ CH 1 — Mute + Solo
        findViewById<ToggleButton>(R.id.ch1_mute).setOnCheckedChangeListener { _, isChecked ->
            ch1Volume.alpha = if (isChecked) 0.3f else 1.0f
        }

        // ==============================================
        // ✅ CHANNEL 2 — GUITAR / IN
        // ==============================================
        val ch2Volume = findViewById<SeekBar>(R.id.ch2_volume)
        val ch2VolumeText = findViewById<TextView>(R.id.ch2_volume_text)

        ch2Volume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val db = ((progress - 50) * 0.6).roundToInt()
                ch2VolumeText.text = if (db >= 0) "+$db dB" else "$db dB"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        findViewById<ToggleButton>(R.id.ch2_mute).setOnCheckedChangeListener { _, isChecked ->
            ch2Volume.alpha = if (isChecked) 0.3f else 1.0f
        }

        // ==============================================
        // ✅ MASTER VOLUME
        // ==============================================
        val masterVolume = findViewById<SeekBar>(R.id.master_volume)
        val masterVolumeText = findViewById<TextView>(R.id.master_volume_text)

        masterVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val db = ((progress - 50) * 0.6).roundToInt()
                masterVolumeText.text = "MASTER: ${if (db >= 0) "+$db" else "$db"} dB"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // ==============================================
        // ✅ EFFECTS — Reverb / Delay / Distortion
        // ==============================================
        findViewById<ToggleButton>(R.id.effect_reverb).setOnCheckedChangeListener { button, isChecked ->
            button.setBackgroundColor(if (isChecked) 0xFF40E0D0.toInt() else 0xFF2A2A3C.toInt())
        }
        findViewById<ToggleButton>(R.id.effect_delay).setOnCheckedChangeListener { button, isChecked ->
            button.setBackgroundColor(if (isChecked) 0xFF40E0D0.toInt() else 0xFF2A2A3C.toInt())
        }
        findViewById<ToggleButton>(R.id.effect_distortion).setOnCheckedChangeListener { button, isChecked ->
            button.setBackgroundColor(if (isChecked) 0xFFFF6B6B.toInt() else 0xFF2A2A3C.toInt())
        }
    }
}
