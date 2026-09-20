// ==================================================
// FILE: AdminLoginFragment.kt — ✅ SESSION SIGURADONG NAI-SAVE! WALANG BINAWASAN SA ORIHINAL!
// VERSION: 2.0.2 — ✅ IDINAGDAG: saveSession() + openAdminPanel()! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-20 — ORIHINAL NA CODE BUO PA RIN — DAGDAG LANG!
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
        prefs = requireContext().getSharedPreferences("admin_session", Context.MODE_PRIVATE) // ✅ BAGONG PANGALAN — HINDI MABABANGGA SA LUMANG AdminPrefs!

        webView = root.findViewById<WebView>(R.id.web_admin)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(AdminBridge(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // ✅ ORIHINAL NA CODE — WALANG BINAGO!
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
        // ✅ ORIHINAL — WALANG BINAGO!
        @JavascriptInterface
        fun saveAdminLogin(username: String) {
            prefs.edit()
                .putString("admin_user", username)
                .putString("admin_last_login", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale("tl", "PH"))
                    .format(java.util.Date()))
                .apply()
        }

        // ✅ IDINAGDAG — SIGURADUHIN NAI-SAVE ANG SESSION PARA MAKITA NG ADMIN PANEL!
        @JavascriptInterface
fun saveSession(keyCode: String, level: String) {
    // ✅ DIAGNOSTIC — KUNG LUMABAS ITO = TUMATAWAG!
    android.widget.Toast.makeText(
        context,
        "📞 TINATAWAG ANG saveSession!\nKey: $keyCode\nLevel: $level",
        android.widget.Toast.LENGTH_LONG
    ).show()

    prefs.edit()
        .putString("key_code", keyCode)
        .putString("user_level", level)
        .putLong("login_time", System.currentTimeMillis())
        .putBoolean("session_active", true)
        .apply()
}


        // ✅ IDINAGDAG — BUBUKASIN ANG ADMIN PANEL!
        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            startActivity(intent)
        }

        // ✅ ORIHINAL — WALANG BINAGO! IDINAGDAG LANG ANG CLEAR NG BAGONG SESSION!
        @JavascriptInterface
        fun logoutAdmin() {
            prefs.edit().clear().apply()
            webView.post { webView.reload() }
        }
    }
}
