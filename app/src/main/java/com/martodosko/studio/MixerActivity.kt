// ==================================================
// FILE: MixerActivity.kt — BUONG LAYOUT SA KOTLIN ✅ WALANG XML!
// VERSION: 1.0.80 — BAGONG PLANO: WALANG fragment_mixer.xml ✅
// UPDATED: 2026-09-13
// PURPOSE: Buong Mixer screen — ginawa sa Kotlin code lang
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // ✅ WALANG XML NA FILE — LAHAT GAGAWIN DITO!
            setContentView(buildMixerLayout())

            Toast.makeText(this, "🎚️ Mixer Screen — LOADED OK", Toast.LENGTH_SHORT).show()
            Log.d("MIXER", "✅ MixerActivity — Buong layout ginawa sa Kotlin!")

        } catch (e: Exception) {
            Log.e("MIXER", "❌ Error: ${e.message}", e)
            Toast.makeText(this, "⚠️ Error — babalik sa Main", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ==================================================
    // ✅ BUONG MIXER LAYOUT — GINAGAWA DITO SA KOTLIN!
    // ==================================================
    private fun buildMixerLayout(): View {
        val scrollView = ScrollView(this)
        scrollView.setBackgroundColor(Color.parseColor("#080810"))
        scrollView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setPadding(48, 48, 48, 48)
        mainLayout.layoutParams = ScrollView.LayoutParams(
            ScrollView.LayoutParams.MATCH_PARENT,
            ScrollView.LayoutParams.WRAP_CONTENT
        )

        // ==============================================
        // HEADER
        // ==============================================
        val title = TextView(this)
        title.text = "🎚️ MIXER — BAGONG PLANO"
        title.setTextColor(Color.parseColor("#40E0D0"))
        title.textSize = 22f
        title.setPadding(0, 0, 0, 32)
        title.gravity = Gravity.CENTER
        mainLayout.addView(title)

        // ==============================================
        // CHANNEL HEADER
        // ==============================================
        val channelHeader = TextView(this)
        channelHeader.text = "🎤 VOCAL CHANNEL"
        channelHeader.setTextColor(Color.parseColor("#40E0D0"))
        channelHeader.textSize = 18f
        channelHeader.setPadding(24, 16, 24, 16)
        channelHeader.setBackgroundColor(Color.parseColor("#12121F"))
        mainLayout.addView(channelHeader)

        // ==============================================
        // ✅ GAIN — UNANG KNOB — BILANG LANG MUNA!
        // ==============================================
        mainLayout.addView(buildKnobRow("GAIN", "0 dB"))

        // ==============================================
        // ⏸️ IBA PANG KNOBS — KOMENTO MUNA — DAHAN-DAHAN!
        // ==============================================
        // mainLayout.addView(buildKnobRow("BASS", "0 dB"))
        // mainLayout.addView(buildKnobRow("LOW-MID", "0 dB"))
        // mainLayout.addView(buildKnobRow("MID", "0 dB"))
        // mainLayout.addView(buildKnobRow("HIGH-MID", "0 dB"))
        // mainLayout.addView(buildKnobRow("TREBLE", "0 dB"))
        // mainLayout.addView(buildKnobRow("PRESENCE", "0 dB"))
        // mainLayout.addView(buildKnobRow("REVERB", "0%"))
        // mainLayout.addView(buildKnobRow("DELAY", "0ms"))
        // mainLayout.addView(buildKnobRow("COMPRESSOR", "0%"))
        // mainLayout.addView(buildKnobRow("PAN", "CENTER"))
        // mainLayout.addView(buildKnobRow("VOLUME", "0 dB"))

        scrollView.addView(mainLayout)
        return scrollView
    }

    // ==================================================
    // ✅ REUSABLE KNOB ROW — WALANG DRAWABLE!
    // ==================================================
    private fun buildKnobRow(labelText: String, valueText: String): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(24, 12, 24, 12)
        row.setBackgroundColor(Color.parseColor("#12121F"))
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(0, 0, 0, 8)
        row.layoutParams = layoutParams

        // LABEL
        val label = TextView(this)
        label.text = labelText
        label.setTextColor(Color.parseColor("#AAAAAA"))
        label.textSize = 14f
        label.width = 240
        row.addView(label)

        // ✅ KNOB — BILOG NA PIHITAN — GINAGAWA SA KOTLIN! WALANG IC_KNOB.XML!
        val knob = TextView(this)
        knob.text = "⚫"
        knob.setTextColor(Color.parseColor("#40E0D0"))
        knob.textSize = 24f
        knob.gravity = Gravity.CENTER
        knob.width = 144
        knob.height = 144
        knob.setBackgroundColor(Color.parseColor("#2A2A3C"))
        row.addView(knob)

        // VALUE
        val value = TextView(this)
        value.text = valueText
        value.setTextColor(Color.parseColor("#FFFFFF"))
        value.textSize = 14f
        value.width = 160
        value.gravity = Gravity.END
        row.addView(value)

        return row
    }
}
