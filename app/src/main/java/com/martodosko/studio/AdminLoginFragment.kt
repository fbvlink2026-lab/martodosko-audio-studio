// ==================================================
// FILE: AdminLoginFragment.kt — ✅ PURONG HTML + JAVASCRIPT INTERFACE!
// VERSION: 2.0.1 — ✅ IDINAGDAG LANG: openAdminPanel()! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-19 — WALANG BINAWASAN, WALANG BINAGO — BUTTON LANG ANG GINAWANG GUMAGANA!
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
    private lateinit var webView: WebView // ✅ IDINAGDAG — kailangan para sa logout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_admin_login, container, false)
        prefs = requireContext().getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)

        webView = root.findViewById<WebView>(R.id.web_admin) // ✅ GINAWING GLOBAL
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
                .putString("admin_last_login", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale("tl", "PH"))
                    .format(java.util.Date()))
                .apply()
        }

        // ✅ IDINAGDAG — ITO ANG KULANG! BUBUKASIN ANG ADMIN PANEL! WALANG IBANG PINAGBAGO!
        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            startActivity(intent)
        }

        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
            webView.post { webView.reload() } // ✅ IDINAGDAG — i-refresh ang page pagkatapos mag-logout
        }
    }
}
