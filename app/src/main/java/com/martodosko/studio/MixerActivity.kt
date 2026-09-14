// ==================================================
// FILE: MixerActivity.kt — SIMPLE ✅
// VERSION: 2.0.0 — 0=ITAAS, 0-50, BAWAT 5!
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

            // ✅ SIMULA = 0 — NASA ITAAS
            gainKnob.minValue = 0f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f
            gainValue.text = "0"

            gainKnob.onValueChange = { newVal ->
                gainValue.text = "${newVal.roundToInt()}"
            }

            Toast.makeText(this, "Ready — 0=ITAAS, 0-50", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
