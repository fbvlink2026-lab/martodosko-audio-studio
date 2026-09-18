// ==================================================
// FILE: AbisoFragment.kt — ✅ PURONG HTML NA MAY DISENYO! ISA NA LANG!
// VERSION: 1.1.0 — WALANG IBANG FRAGMENT! TUGMA SA SIDEMENU!
// UPDATED: 2026-09-19
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.text.HtmlCompat

class AbisoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_abiso, container, false)

        val tvContent = root.findViewById<TextView>(R.id.tv_abiso_html)
        val htmlText = getString(R.string.abiso_proyekto)
        tvContent.text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT)

        return root
    }
}
