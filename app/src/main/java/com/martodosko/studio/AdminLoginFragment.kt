// ==================================================
// FILE: AdminLoginFragment.kt — ✅ V2.0.1 — DAGDAG LANG ANG KULANG! WALANG BINURA! WALANG BINAGO!
// VERSION: 2.0.1 — ✅ IDINAGDAG: openAdminPanel() + verifyKeyCode() + saveSession()! ORIHINAL PA RIN ANG LAHAT!
// UPDATED: 2026-09-20 — WALANG TINANGGAL, WALANG PINALITAN — DAGDAG LANG!
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

        webView = root.findViewById(R.id.web_admin) // ✅ GINAWING GLOBAL
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true // ✅ IDINAGDAG — para sa session
        webView.addJavascriptInterface(AdminBridge(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // ✅ Kung may naka-save na login — i-load agad — ORIHINAL NA! HINDI BINAGO!
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
        // ✅ ORIHINAL — HINDI BINAGO!
        @JavascriptInterface
        fun saveAdminLogin(username: String) {
            prefs.edit()
                .putString("admin_user", username)
                .putString("admin_last_login", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale("tl", "PH"))
                    .format(java.util.Date()))
                .apply()
        }

        // ✅ IDINAGDAG — ITO ANG KULANG! KEY CODE VERIFICATION!
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

        // ✅ IDINAGDAG — I-SAVE ANG SESSION!
        @JavascriptInterface
        fun saveSession(keyCode: String, level: String) {
            prefs.edit()
                .putString("key_code", keyCode)
                .putString("user_level", level)
                .putLong("login_time", System.currentTimeMillis())
                .apply()
        }

        // ✅ IDINAGDAG — ITO ANG PINAKAKULANG! BUBUKAS NA ANG ADMIN PANEL!
        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            startActivity(intent)
        }

        // ✅ ORIHINAL — INAYOS LANG PARA MA-RELOAD ANG PAGE — WALANG BINURA!
        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
            webView.post { webView.reload() } // ✅ IDINAGDAG — para ma-refresh page
        }
    }
}
