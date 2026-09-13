// ==================================================
// FILE: MixerActivity.kt — 0-10 SCALE ✅ TUGMA SA KNOB MARKA!
// VERSION: 1.0.100 — 0=ibaba-kaliwa, 10=ibaba-kanan, WALANG dB!
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
        setContentView(R.layout.activity_mixer) // ✅ XML — SIMPLE!

        try {
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            val gainValue = findViewById<TextView>(R.id.tv_gain_value)

            // ✅ 0 hanggang 10 — TUGMA SA MARKA SA PALIGID NG KNOB!
            // 0 = IBABA-KALIWA ↙️ , 10 = IBABA-KANAN ↘️
            gainKnob.minValue = 0f
            gainKnob.maxValue = 10f
            gainKnob.value = 0f  // ✅ Simula sa 0 — ibaba-kaliwa

            gainKnob.onValueChange = { newVal ->
                gainValue.text = "${newVal.roundToInt()}" // ✅ Numero lang — WALANG dB!
            }

            Toast.makeText(this, "🎚️ 0-10 Scale — OK!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
