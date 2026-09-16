// ==================================================
// FILE: JoinUsFragment.kt — ✅ JOIN US PAGE!
// VERSION: 1.0.0 — KAPAG PININDOT ANG JOIN US BUTTON!
// UPDATED: 2026-09-17
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class JoinUsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(48, 48, 48, 48)
        root.setBackgroundColor(0xFF081218.toInt())

        val title = TextView(requireContext())
        title.text = "🌐 Join Us"
        title.textSize = 28f
        title.setTextColor(0xFF00BFFF.toInt())
        title.setPadding(0, 0, 0, 40)
        root.addView(title)

        val content = TextView(requireContext())
        content.text = "Sumali sa aming komunidad!\n\nFacebook: Martodosko Studio"
        content.textSize = 15f
        content.setTextColor(0xFFCCCCCC.toInt())
        root.addView(content)

        return root
    }
}
