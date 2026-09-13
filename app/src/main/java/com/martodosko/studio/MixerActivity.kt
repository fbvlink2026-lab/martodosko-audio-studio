// ==================================================
// FILE: MixerActivity.kt — GUMAGAMIT NA NG TOTOONG KNOB ✅
// VERSION: 1.0.81 — KnobView mula sa ControlsActivity.kt
// UPDATED: 2026-09-13
// PURPOSE: Totoong Knob — pwedeng paikutin! Dahan-dahan lang!
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
            setContentView(buildMixerLayout())
            Toast.makeText(this, "🎚️ Mixer — Totoong Knob OK!", Toast.LENGTH_SHORT).show()
            Log.d("MIXER", "✅ MixerActivity — KnobView connected!")

        } catch (e: Exception) {
            Log.e("MIXER", "❌ Error: ${e.message}", e)
            Toast.makeText(this, "⚠️ Error — babalik sa Main", Toast.LENGTH_LONG).show()
            finish()
        }
    }

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

        // HEADER
        val title = TextView(this)
        title.text = "🎚️ MIXER — TOTOONG KNOB"
        title.setTextColor(Color.parseColor("#40E0D0"))
        title.textSize = 22f
        title.setPadding(0, 0, 0, 32)
        title.gravity = Gravity.CENTER
        mainLayout.addView(title)

        // CHANNEL HEADER
        val channelHeader = TextView(this)
        channelHeader.text = "🎤 VOCAL CHANNEL"
        channelHeader.setTextColor(Color.parseColor("#40E0D0"))
        channelHeader.textSize = 18f
        channelHeader.setPadding(24, 16, 24, 16)
        channelHeader.setBackgroundColor(Color.parseColor("#12121F"))
        mainLayout.addView(channelHeader)

        // ==============================================
        // ✅ GAIN — TOTOONG KNOBVIEW! PWEDENG PAIKUTIN!
        // ==============================================
        mainLayout.addView(buildKnobRow("GAIN", 0f, "dB"))

        // ==============================================
        // ⏸️ IBA — KOMENTO MUNA — DAHAN-DAHAN!
        // ==============================================
        // mainLayout.addView(buildKnobRow("BASS", 0f, "dB"))
        // mainLayout.addView(buildKnobRow("LOW-MID", 0f, "dB"))
        // mainLayout.addView(buildKnobRow("MID", 0f, "dB"))
        // mainLayout.addView(buildKnobRow("HIGH-MID", 0f, "dB"))
        // mainLayout.addView(buildKnobRow("TREBLE", 0f, "dB"))
        // mainLayout.addView(buildKnobRow("PRESENCE", 0f, "dB"))
        // mainLayout.addView(buildKnobRow("REVERB", 0f, "%"))
        // mainLayout.addView(buildKnobRow("DELAY", 0f, "ms"))
        // mainLayout.addView(buildKnobRow("COMPRESSOR", 0f, "%"))
        // mainLayout.addView(buildKnobRow("PAN", 50f, ""))
        // mainLayout.addView(buildKnobRow("VOLUME", 0f, "dB"))

        scrollView.addView(mainLayout)
        return scrollView
    }

    // ==================================================
    // ✅ BUILD KNOB ROW — GUMAGAMIT NA NG KnobView!
    // ==================================================
    private fun buildKnobRow(
        labelText: String,
        initialValue: Float,
        unit: String
    ): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(24, 16, 24, 16)
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
        label.textStyle = android.graphics.Typeface.BOLD
        label.width = 240
        row.addView(label)

        // ✅ TOTOONG KNOBVIEW — MULA SA ControlsActivity.kt!
        val knob = KnobView(this)
        knob.layoutParams = LinearLayout.LayoutParams(144, 144)
        knob.minValue = -50f
        knob.maxValue = 50f
        knob.value = initialValue
        row.addView(knob)

        // VALUE DISPLAY — NAGBABAGO PAG PIHITIN ANG KNOB!
        val value = TextView(this)
        value.text = "${initialValue.roundToInt()} $unit"
        value.setTextColor(Color.parseColor("#FFFFFF"))
        value.textSize = 14f
        value.width = 160
        value.gravity = Gravity.END
        row.addView(value)

        // ✅ UPDATE VALUE KAPAG PINIHIT ANG KNOB!
        knob.onValueChange = { newVal ->
            value.text = "${newVal.roundToInt()} $unit"
        }

        return row
    }
}
