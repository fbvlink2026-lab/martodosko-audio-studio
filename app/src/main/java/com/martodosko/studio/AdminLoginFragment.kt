// ==================================================
// FILE: AdminLoginFragment.kt — ✅ AYUSIN: verifyKeyCode + saveSession + TAMA ANG INTERFACE!
// VERSION: 2.1.0 — ✅ KUMPLETO NA! WALANG KULANG! TATAWAG NA ANG saveSession!
// UPDATED: 2026-09-20 — ITO LANG ANG KAILANGAN PALITAN!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment

class AdminLoginFragment : Fragment() {

    private lateinit var prefs: SharedPreferences
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_admin_login, container, false)
        prefs = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)

        webView = root.findViewById<WebView>(R.id.web_admin)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(AdminBridge(), "Android") // ✅ EKSAKTONG "Android" — HINDI IBANG PANGALAN!
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl("file:///android_asset/admin_login.html")

        return root
    }

    inner class AdminBridge {

        // ✅ KULANG ITO KANINA — WALANG verifyKeyCode! ITO ANG DAHILAN!
        @JavascriptInterface
        fun verifyKeyCode(input: String): String {
            val key = input.uppercase()
            val valid = when {
                key.startsWith("MARTODOSKO-OWNER-") || key.startsWith("OWNER-") -> true
                key.startsWith("ADMIN-") -> true
                key.startsWith("MEMBER-") -> true
                else -> false
            }
            val level = when {
                key.startsWith("MARTODOSKO-OWNER-") || key.startsWith("OWNER-") -> "OWNER"
                key.startsWith("ADMIN-") -> "ADMIN"
                key.startsWith("MEMBER-") -> "MEMBER"
                else -> "MEMBER"
            }
            return """{"valid":$valid,"level":"$level"}"""
        }

        // ✅ MAY TOAST NA — MALALAMAN KUNG TUMATAWAG!
        @JavascriptInterface
        fun saveSession(keyCode: String, level: String) {
            Toast.makeText(requireContext(), "✅ NAI-SAVE: $level | $keyCode", Toast.LENGTH_LONG).show()
            prefs.edit()
                .putString("key_code", keyCode)
                .putString("user_level", level)
                .putLong("login_time", System.currentTimeMillis())
                .putBoolean("session_active", true)
                .apply()
        }

        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            startActivity(intent)
        }

        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
            webView.post { webView.reload() }
        }
    }
}
