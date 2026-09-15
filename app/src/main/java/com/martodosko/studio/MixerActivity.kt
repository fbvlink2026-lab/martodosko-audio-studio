// ==================================================
// FILE: MixerActivity.kt — HOLDER NA LANG! TAWAGIN NA LANG!
// VERSION: 4.8.0 — Nasa assets/ ang buong code ng controls!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle

class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ TAWAGIN NA LANG — buong code nasa assets/controls/
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f

            // ✅ DAGDAGAN MO NA LANG DITO — HINDI NA MAGHABA!
            // val bassKnob = findViewById<KnobView>(R.id.knob_bass)
            // val masterSlider = findViewById<HorizontalSliderView>(R.id.slider_master)

        } catch (e: Exception) {
            android.widget.Toast.makeText(this, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
