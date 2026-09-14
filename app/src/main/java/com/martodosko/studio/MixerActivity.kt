// ==================================================
// FILE: MixerActivity.kt - 0 = IBABA ✅
// VERSION: 2.0.0 - SIMPLE, TAMA ANG SIMULA
// UPDATED: 2026-09-14
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class MixerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            val gainValue = findViewById<TextView>(R.id.tv_gain_value)

            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f  // ✅ SIMULA = 0 — NASA IBABA!
            gainValue.text = "0 dB"

            gainKnob.onValueChange = { newVal ->
                gainValue.text = "${newVal.roundToInt()} dB"
            }

            Toast.makeText(this, "Mixer Ready — 0 = IBABA", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
