// ==================================================
// FILE: AdminLoginFragment.kt — ✅ NAILAGAY NA ANG openAdminPanel()! BUBUKAS NA!
// VERSION: 2.1.0 — ✅ IDINAGDAG: openAdminPanel() + verifyKeyCode() + saveSession()! WALANG BINURA!
// UPDATED: 2026-09-20 — KASAMA NA ANG LAHAT NG KAILANGANG BRIDGE!
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
    private lateinit var webView: WebView // ✅ GINAWING GLOBAL — para magamit sa logout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_admin_login, container, false)
        prefs = requireContext().getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)

        webView = root.findViewById<WebView>(R.id.web_admin) // ✅ GLOBAL NA
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true // ✅ IDINAGDAG — para sa session
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

        // ✅ IDINAGDAG — ITO ANG KULANG! BUBUKASIN ANG ADMIN PANEL!
        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            startActivity(intent)
        }

        // ✅ IDINAGDAG — KEY CODE VERIFICATION — tugma sa HTML!
        @JavascriptInterface
        fun verifyKeyCode(input: String): String {
            val trimmed = input.uppercase().trim()
            val result = when {
                trimmed.startsWith("MARTODOSKO-OWNER-") || trimmed.startsWith("OWNER-") ->
                    """{"valid":true,"level":"OWNER"}"""
                trimmed.startsWith("ADMIN-") -> """{"valid":true,"level":"ADMIN"}"""
                trimmed.startsWith("MEMBER-") -> """{"valid":true,"level":"MEMBER"}"""
                else -> """{"valid":false,"level":null}"""
            }
            return result
        }

        // ✅ IDINAGDAG — I-SAVE ANG SESSION — tugma sa HTML!
        @JavascriptInterface
        fun saveSession(keyCode: String, level: String) {
            prefs.edit()
                .putString("key_code", keyCode)
                .putString("user_level", level)
                .putLong("login_time", System.currentTimeMillis())
                .apply()
        }

        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
            // ✅ I-refresh ang page pagkatapos mag-logout
            webView.post { webView.reload() }
        }
    }
}
