// ==================================================
// FILE: AbisoFragment.kt — ✅ PURONG HTML + COLLAPSE/EXPAND!
// VERSION: 2.0.0 — WEBVIEW → abiso.html! MAY MAGANDANG DISENYO!
// UPDATED: 2026-09-19
// ==================================================
package com.martodosko.studio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment

class AbisoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_abiso, container, false)

        val webView = root.findViewById<WebView>(R.id.web_abiso)
        webView.settings.javaScriptEnabled = true // ✅ Para gumana ang toggle
        webView.webViewClient = object : WebViewClient() {}
        webView.loadUrl("file:///android_asset/abiso.html") // ✅ I-load ang HTML file

        return root
    }
}
