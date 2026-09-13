// ==================================================
// FILE: MixerActivity.kt — ✅ 0&10 PINAKA-IBABA + NANDOON ANG dB!
// VERSION: 1.0.104 — TUGMA SA KNOB: 0⬇️ 10⬇️ magkatabi, LAHAT 0-10 LITAW!
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

            // ✅ 0 hanggang 10 — TUGMA SA KNOB:
            // 0 = PINAKA-IBABA KALIWA ⬇️ , 10 = PINAKA-IBABA KANAN ⬇️
            // Magkatabi lang ang 0 at 10 — may kaunting pagitan lang!
            // LAHAT NG NUMERO 0-10 — LITAW LAHAT! Walang nakatago!
            gainKnob.minValue = 0f
            gainKnob.maxValue = 10f
            gainKnob.value = 0f  // ✅ Simula sa 0 — PINAKA-IBABA KALIWA

            gainKnob.onValueChange = { newVal ->
                // ✅ NANDOON ANG dB — HINDI TINANGGAL!
                // Halimbawa: "0 dB", "5 dB", "10 dB"
                gainValue.text = "${newVal.roundToInt()} dB"
            }

            Toast.makeText(this, "🎚️ 0-10 — 0&10 PINAKA-IBABA + dB — OK!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
