// ==================================================
// FILE: MixerActivity.kt — NANDOON ANG dB! ✅ 0-10 + dB
// VERSION: 1.0.102 — NUMERO SA PALIGID HINDI GUMAGALAW, MAY dB SA HALAGA!
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

            // ✅ 0 hanggang 10 — NUMERO SA PALIGID HINDI GUMAGALAW!
            // 0 = ibaba-kaliwa ↙️ , 10 = ibaba-kanan ↘️
            gainKnob.minValue = 0f
            gainKnob.maxValue = 10f
            gainKnob.value = 0f

            gainKnob.onValueChange = { newVal ->
                // ✅ NANDOON ANG dB — HINDI TINANGGAL!
                gainValue.text = "${newVal.roundToInt()} dB"
            }

            Toast.makeText(this, "🎚️ 0-10 + dB — OK!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
