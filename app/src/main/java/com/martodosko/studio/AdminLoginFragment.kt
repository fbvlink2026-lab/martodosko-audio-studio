// ==================================================
// FILE: AdminLoginFragment.kt — ✅ TUMATANGGAP NA ULIT! TAMA ANG JSON! MAY KAPANGYARIHAN NA!
// VERSION: 2.0.7 — ✅ AYUS ANG JSON FORMAT! BALIK SA TAMA! WALANG IBANG BINAGO!
// UPDATED: 2026-09-20 — TUMATANGGAP NA ANG KEY CODE + NAI-SAVE NA ANG user_level!
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_admin_login, container, false)
        prefs = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)

        val webView = root.findViewById<WebView>(R.id.web_admin)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(AdminBridge(), "Android")
        webView.webViewClient = object : WebViewClient() {}
        webView.loadUrl("file:///android_asset/admin_login.html")

        return root
    }

    inner class AdminBridge {

        // ✅ TAMA ANG JSON FORMAT — SIGURADONG MAPARSE NG HTML!
        @JavascriptInterface
        fun verifyKeyCode(input: String): String {
            val cleanInput = input.uppercase().trim().replace("\\s+".toRegex(), "")
            
            return when {
                cleanInput.startsWith("MARTODOSKO-OWNER-") || cleanInput.startsWith("OWNER-") -> 
                    """{"valid":true,"level":"OWNER"}"""
                cleanInput.startsWith("ADMIN-") -> 
                    """{"valid":true,"level":"ADMIN"}"""
                cleanInput.startsWith("MEMBER-") -> 
                    """{"valid":true,"level":"MEMBER"}"""
                else -> 
                    """{"valid":false,"level":null}"""
            }
        }

        // ✅ TINATAWAG NG HTML PAGKATAPOS MAG-VERIFY!
        @JavascriptInterface
        fun saveSession(keyCode: String, level: String) {
            prefs.edit()
                .putString("admin_user", keyCode)
                .putString("user_level", level)
                .putString("admin_last_login", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale("tl", "PH"))
                    .format(java.util.Date()))
                .apply()
        }

        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
        }
    }
}
