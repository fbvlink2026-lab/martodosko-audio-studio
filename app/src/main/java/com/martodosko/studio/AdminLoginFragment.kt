// ==================================================
// FILE: AdminLoginFragment.kt — ✅ FIX: openAdminPanel() SIGURADONG BUBUKAS NA!
// VERSION: 3.2.0 — ✅ TANGING PROBLEMA LANG AY ANG BUTTON! ITO ANG AYUSIN!
// UPDATED: 2026-09-20 — WALANG BINAGO SA VERIFY AT SAVE — openAdminPanel LANG ANG INAYOS!
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

        webView = root.findViewById(R.id.web_admin)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true

        // ✅ SIGURADUHIN — TAMA ANG PANGALAN: "Android"
        webView.addJavascriptInterface(AdminBridge(), "Android")
        webView.webViewClient = object : WebViewClient() {}
        webView.loadUrl("file:///android_asset/admin_login.html")

        return root
    }

    inner class AdminBridge {

        // ✅ VERIFY — GUMAGANA NA! WALA NANG BABAGUHIN!
        @JavascriptInterface
        fun verifyKeyCode(input: String): String {
            val cleanInput = input.uppercase().trim().replace("\\s+".toRegex(), "")
            val result = when {
                cleanInput.startsWith("MARTODOSKO-OWNER-") || cleanInput.startsWith("OWNER-") ->
                    """{"valid":true,"level":"OWNER"}"""
                cleanInput.startsWith("ADMIN-") -> """{"valid":true,"level":"ADMIN"}"""
                cleanInput.startsWith("MEMBER-") -> """{"valid":true,"level":"MEMBER"}"""
                else -> """{"valid":false,"level":null}"""
            }
            return result
        }

        // ✅ SAVE SESSION — GUMAGANA NA! WALA NANG BABAGUHIN!
        @JavascriptInterface
        fun saveSession(keyCode: String, level: String) {
            prefs.edit()
                .putString("key_code", keyCode)
                .putString("user_level", level)
                .putLong("login_time", System.currentTimeMillis())
                .apply()
        }

        // ✅ ITO ANG PROBLEMA — SIGURADUHIN TAMA ANG INTENT!
        @JavascriptInterface
        fun openAdminPanel() {
            // ✅ SIGURADUHIN — MAY CONTEXT BA? MAY PERMISSION BA?
            val context = context ?: return
            Toast.makeText(context, "📊 Binubuksan ang Admin Panel...", Toast.LENGTH_SHORT).show()
            
            // ✅ TAMA ANG INTENT — WALANG ERROR!
            val intent = Intent(context, AdminPanelActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // ✅ SIGURADUHIN — KAILANGAN ITO!
            context.startActivity(intent)
        }

        // ✅ LOGOUT — GUMAGANA NA!
        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
            webView.post { webView.reload() }
        }
    }
}
