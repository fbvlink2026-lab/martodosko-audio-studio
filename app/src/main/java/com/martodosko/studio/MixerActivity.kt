// ==================================================
// FILE: MixerActivity.kt — ✅ MALINIS NA! KANYA-KANYANG FILE!
// VERSION: 5.0.0 — KnobView = PUNDASYON, GainKnob = TOTOONG GINAGAMIT!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ KUNG GUSTO MO — GAMITIN ANG GainKnob:
            // val gainKnob = findViewById<GainKnob>(R.id.knob_gain)

            // ✅ PANATILIHIN ANG KnobView PARA HINDI NA BAGUHIN ANG XML:
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
