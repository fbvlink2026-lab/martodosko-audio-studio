// ==================================================
// FILE: MixerActivity.kt — ✅ PINASIMPLE! IMPORT LANG!
// VERSION: 5.0.0 — HANDA NA SA DAGDAG! WALANG CRASH!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.martodosko.studio.controls.GainKnob           // ✅ GAIN — SARILING FILE!
import com.martodosko.studio.controls.HorizontalSliderView // ✅ SLIDER — SARILING FILE!
import com.martodosko.studio.controls.ToggleButtonView     // ✅ BUTTON — SARILING FILE!

class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ GAIN — SIGURADONG GUMAGANA!
            val gainKnob = findViewById<GainKnob>(R.id.knob_gain)
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f

            // ✅ HANDANG-HANDA NA SA DAGDAG!
            // val reverbKnob = findViewById<ReverbKnob>(R.id.knob_reverb)
            // val masterSlider = findViewById<HorizontalSliderView>(R.id.slider_master)
            // val stereoBtn = findViewById<ToggleButtonView>(R.id.btn_stereo)

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
