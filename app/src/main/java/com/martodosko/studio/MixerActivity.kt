// ==================================================
// FILE: MixerActivity.kt — ✅ TUGMA SA KNOB + AUTO-UPDATE + WALANG CRASH!
// VERSION: 1.0.102 — -50 hanggang 50, MAY INITIAL VALUE, TAMA ANG LISTENER!
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

            // ✅ I-SET MUNA ANG SAKLAW BAGO ANG LAHAT
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f

            // ✅ INITIAL VALUE — 0 dB SA SIMULA
            gainKnob.value = 0f

            // ✅ AGAD IPAKITA ANG INITIAL VALUE — HINDI HINTAY ANG PAG-UMAYOS!
            gainValue.text = "0 dB"

            // ✅ LISTENER — TUWING NAGBABAGO ANG HALAGA, AAGAD MAG-UUPDATE!
            gainKnob.onValueChange = { newVal ->
                gainValue.text = "${newVal.roundToInt()} dB"
            }

            Toast.makeText(this, "🎚️ Mixer Ready — Saklaw: -50 hanggang 50 dB", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Error sa Mixer: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace() // ✅ Makikita sa log kung ano ang kulang
            finish()
        }
    }
}
