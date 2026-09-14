// ==================================================
// FILE: MixerActivity.kt — ✅ TUGMA SA BAGONG KNOB!
// VERSION: 2.0.0 — 0=ITAAS, BAWAT 5, -50 HANGGANG +50, SIMULA=0dB
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
            // ✅ HANAPIN ANG MGA ELEMENTO
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            val gainValue = findViewById<TextView>(R.id.tv_gain_value)

            // ✅ SAKLAW: -50 hanggang +50
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f

            // ✅ SIMULA = 0 dB — NASA ITAAS AGAD!
            gainKnob.value = 0f
            gainValue.text = "0 dB"

            // ✅ TUWING UMIKOT — AGAD MAG-UUPDATE ANG HALAGA!
            gainKnob.onValueChange = { newVal ->
                gainValue.text = "${newVal.roundToInt()} dB"
            }

            Toast.makeText(this, "🎚️ Mixer Ready — Saklaw: -50 hanggang +50 dB", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            finish()
        }
    }
}
