// ==================================================
// FILE: MixerActivity.kt — FINAL ✅ SIGURADONG MAGBUBUO!
// VERSION: 1.0.90 — TAMA NA ANG LAHAT NG SYNTAX!
// UPDATED: 2026-09-14
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(buildMixerLayout())
            android.widget.Toast.makeText(this, "🎚️ Mixer — OK!", android.widget.Toast.LENGTH_SHORT).show()
            Log.d("MIXER", "✅ MixerActivity loaded!")
        } catch (e: Exception) {
            Log.e("MIXER", "❌ Error: ${e.message}", e)
            android.widget.Toast.makeText(this, "⚠️ Error — babalik sa Main", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun buildMixerLayout(): View {
        // ✅ SCROLLVIEW — IHIWALAY MUNA ANG PARAMS!
        val scrollView = ScrollView(this)
        scrollView.setBackgroundColor(Color.parseColor("#080810"))
        // ✅ TAMA: GUMAGAWA MUNA NG VARIABLE BAGO I-ASSIGN!
        val scrollParams = ScrollView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        scrollView.layoutParams = scrollParams

        // ✅ MAIN LAYOUT — IHIWALAY MUNA ANG PARAMS!
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.setPadding(48, 48, 48, 48)
        // ✅ TAMA: GUMAGAWA MUNA NG VARIABLE BAGO I-ASSIGN!
        val mainParams = ScrollView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        mainLayout.layoutParams = mainParams

        // HEADER
        val title = TextView(this)
        title.text = "🎚️ MIXER"
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

        // ✅ GAIN KNOB
        mainLayout.addView(buildKnobRow("GAIN", 0f, "dB"))

        scrollView.addView(mainLayout)
        return scrollView
    }

    private fun buildKnobRow(
        labelText: String,
        initialValue: Float,
        unit: String
    ): View {
        // ✅ ROW — IHIWALAY MUNA ANG PARAMS!
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(24, 16, 24, 16)
        row.setBackgroundColor(Color.parseColor("#12121F"))
        // ✅ TAMA: GUMAGAWA MUNA NG VARIABLE BAGO I-ASSIGN!
        val rowParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        rowParams.setMargins(0, 0, 0, 8)
        row.layoutParams = rowParams

        // LABEL
        val label = TextView(this)
        label.text = labelText
        label.setTextColor(Color.parseColor("#AAAAAA"))
        label.textSize = 14f
        label.setTypeface(null, Typeface.BOLD)
        label.width = 240
        row.addView(label)

        // ✅ KNOB
        val knob = KnobView(this)
        val knobParams = LinearLayout.LayoutParams(144, 144)
        knob.layoutParams = knobParams
        knob.minValue = -50f
        knob.maxValue = 50f
        knob.value = initialValue
        row.addView(knob)

        // VALUE
        val value = TextView(this)
        value.text = "${initialValue.roundToInt()} $unit"
        value.setTextColor(Color.parseColor("#FFFFFF"))
        value.textSize = 14f
        value.width = 160
        value.gravity = Gravity.END
        row.addView(value)

        knob.onValueChange = { newVal ->
            value.text = "${newVal.roundToInt()} $unit"
        }

        return row
    }
}
