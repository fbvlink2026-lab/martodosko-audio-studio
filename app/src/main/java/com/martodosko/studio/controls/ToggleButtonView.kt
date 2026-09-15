// ==================================================
// FILE: ToggleButtonView.kt — ✅ BUTTON LANG! SARILING FILE!
// VERSION: 1.0.0 — HINIHIWALAY NA!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio.controls

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class ToggleButtonView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var isOn: Boolean = false
        set(v) {
            field = v
            update()
            onToggle?.invoke(field)
        }
    var textLabel: String = ""
        set(v) {
            field = v
            tv.text = v
        }
    var onToggle: ((Boolean) -> Unit)? = null

    private lateinit var tv: TextView
    private lateinit var dot: View

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER_VERTICAL
        setPadding(24, 12, 24, 12)
        setBackgroundColor(Color.parseColor("#12121F"))
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        dot = View(context)
        val p = LayoutParams(32, 32)
        p.leftMargin = 0
        p.topMargin = 0
        p.rightMargin = 16
        p.bottomMargin = 0
        dot.layoutParams = p
        addView(dot)

        tv = TextView(context)
        tv.textSize = 14f
        tv.setTextColor(Color.WHITE)
        addView(tv)

        setOnClickListener { isOn = !isOn }
        update()
    }

    private fun update() {
        dot.setBackgroundColor(if (isOn) Color.parseColor("#40E0D0") else Color.parseColor("#333344"))
        setBackgroundColor(if (isOn) Color.parseColor("#1A1A3A") else Color.parseColor("#12121F"))
    }
}
