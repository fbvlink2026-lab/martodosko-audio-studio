// ==================================================
// FILE: AdminLoginFragment.kt — ✅ UPDATE: KEY CODE LANG! WALANG USERNAME/PASSWORD!
// VERSION: 3.0.0 — ✅ TUGMA SA BAGONG admin_login.html! verifyKeyCode + saveSession + openAdminPanel!
// UPDATED: 2026-09-20 — TINANGGAL ANG LUMANG USERNAME/PASSWORD! NAIWASAN ANG CONFLICT!
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

        webView.addJavascriptInterface(AdminBridge(), "Android")
        webView.webViewClient = object : WebViewClient() {}

        webView.loadUrl("file:///android_asset/admin_login.html")
        return root
    }

    inner class AdminBridge {

        // ✅ KEY CODE VERIFICATION — TUGMA SA BAGONG HTML! MAS MALUWAG NA!
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

        // ✅ I-SAVE ANG SESSION — KEY CODE + LEVEL LANG! WALANG USERNAME!
        @JavascriptInterface
        fun saveSession(keyCode: String, level: String) {
            prefs.edit()
                .putString("key_code", keyCode)
                .putString("user_level", level)
                .putLong("login_time", System.currentTimeMillis())
                .apply()
        }

        // ✅ BUBUKASIN ANG ADMIN PANEL!
        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            startActivity(intent)
        }

        // ✅ MAG-LOGOUT — BURAHIN ANG SESSION AT I-REFRESH!
        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
            webView.post { webView.reload() }
        }
    }
}
