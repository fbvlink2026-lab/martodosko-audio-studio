// ==================================================
// FILE: MixerActivity.kt — NANDOON ANG dB ✅ TUGMA SA KNOB!
// VERSION: 1.0.103 — 0=ibaba ↓, 1-10 lahat naroon, may dB!
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

            // ✅ 0 = PINAKA-IBABA ↓, 10 = KANAN-IBABA ↘️
            gainKnob.minValue = 0f
            gainKnob.maxValue = 10f
            gainKnob.value = 0f

            gainKnob.onValueChange = { newVal ->
                gainValue.text = "${newVal.roundToInt()} dB" // ✅ NANDOON ANG dB!
            }

            Toast.makeText(this, "🎚️ 0=ibaba ↓ — OK!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
