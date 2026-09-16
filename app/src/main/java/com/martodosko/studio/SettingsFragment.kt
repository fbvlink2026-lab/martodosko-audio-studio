// ==================================================
// FILE: SettingsFragment.kt — ✅ SETTINGS PAGE! DEFAULT AGAD!
// VERSION: 1.0.0 — KUSANG LALABAS KAPAG WALANG PINILI!
// UPDATED: 2026-09-17
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(48, 48, 48, 48)
        root.setBackgroundColor(0xFF081218.toInt())

        // ✅ TITLE
        val title = TextView(requireContext())
        title.text = "⚙️ Settings"
        title.textSize = 28f
        title.setTextColor(0xFF40E0D0.toInt())
        title.setPadding(0, 0, 0, 40)
        root.addView(title)

        // ✅ EXAMPLE SETTING — DAGDAGAN MO PA!
        val darkMode = TextView(requireContext())
        darkMode.text = "🌙 Dark Mode"
        darkMode.textSize = 18f
        darkMode.setTextColor(0xFFFFFFFF.toInt())
        darkMode.setPadding(0, 20, 0, 20)
        root.addView(darkMode)

        // ✅ DAGDAGAN MO PA NG IBA PANG SETTINGS DITO...

        return root
    }
}
