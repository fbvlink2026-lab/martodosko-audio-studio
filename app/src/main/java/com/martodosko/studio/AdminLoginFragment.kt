// ==================================================
// FILE: AdminLoginFragment.kt — ✅ TUGMA NA SA AdminPanelActivity! "admin_session" + "user_level"!
// VERSION: 2.0.2 — ✅ WALANG BINAWASAN — PANGALAN NG PREFS AT user_level LANG ANG INAYOS!
// UPDATED: 2026-09-20 — ORIHINAL NA CODE + TAMA NA ANG SESSION PARA MAKILALA NG ADMIN PANEL!
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
        
        // ✅ PALITAN — "admin_session" HINDI "AdminPrefs" — TUGMA SA AdminPanelActivity!
        prefs = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE)

        val webView = root.findViewById<WebView>(R.id.web_admin)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(AdminBridge(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // ✅ Kung may naka-save na login — i-load agad
                val savedUser = prefs.getString("admin_user", null)
                if (savedUser != null) {
                    val lastLogin = prefs.getString("admin_last_login", "-")
                    webView.evaluateJavascript("""
                        updateSessionInfo('$lastLogin', 0, 0, 0, 0, '$savedUser');
                    """.trimIndent(), null)
                }
            }
        }
        webView.loadUrl("file:///android_asset/admin_login.html")

        return root
    }

    inner class AdminBridge {
        @JavascriptInterface
        fun saveAdminLogin(username: String) {
            prefs.edit()
                .putString("admin_user", username)
                // ✅ DAGDAG — KUNIN ANG LEVEL MULA SA KEY CODE!
                .putString("user_level", when {
                    username.startsWith("MARTODOSKO-OWNER-") || username.startsWith("OWNER-") -> "OWNER"
                    username.startsWith("ADMIN-") -> "ADMIN"
                    username.startsWith("MEMBER-") -> "MEMBER"
                    else -> "GUEST"
                })
                .putString("admin_last_login", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale("tl", "PH"))
                    .format(java.util.Date()))
                .apply()
        }

        // ✅ DAGDAG — BUBUKASIN ANG ADMIN PANEL!
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
