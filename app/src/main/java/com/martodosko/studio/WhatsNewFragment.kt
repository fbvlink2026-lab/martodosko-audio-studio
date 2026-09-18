// ==================================================
// FILE: WhatsNewFragment.kt — ✅ WHAT'S NEW SCREEN!
// VERSION: 1.0.0 — SIMPLE! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-19
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class WhatsNewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_whats_new, container, false)

        val tvContent = root.findViewById<TextView>(R.id.tv_whatsnew_content)
        tvContent.text = """
🆕 WHAT'S NEW — v1.0.0

✅ Side Menu — Admin Panel + What's New + Exit
✅ Mixer — Gain Knob (-50~+50, 0 sa itaas)
✅ ContentActivity — Settings / Help / Join Us / About
✅ Auto Update — Check mula sa GitHub
✅ Permissions — INTERNET / Download / Install

Darating pa:
🎸 Guitar Effects Channel
📋 Presets Save & Load
🎛️ Higit pang Knobs — Decay, Chorus, Reverb
⚡ Low Latency Audio Engine (C++)

— Martodosko Studio Team
        """.trimIndent()

        return root
    }
}
