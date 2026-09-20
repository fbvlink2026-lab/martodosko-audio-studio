// ==================================================
// FILE: AdminLoginFragment.kt — ✅ INAYOS ANG DALAWANG SANHI! KEY CODE TATANGGAPIN NA! BUBUKAS NA!
// VERSION: 2.1.1 — ✅ TINANGGAL ANG PUWANG SA KEY CODE + MAY FLAG NA SA INTENT!
// UPDATED: 2026-09-20 — ORIHINAL NA STRUCTURE — DALAWANG LINYA LANG ANG INAYOS!
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
        prefs = requireContext().getSharedPreferences("AdminPrefs", Context.MODE_PRIVATE)

        webView = root.findViewById(R.id.web_admin)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(AdminBridge(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
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

        // ✅ INAYOS — MAY FLAG NA! SIGURADONG BUBUKAS NA!
        @JavascriptInterface
        fun openAdminPanel() {
            val intent = Intent(requireContext(), AdminPanelActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // ✅ ITO ANG KULANG!
            startActivity(intent)
        }

        // ✅ INAYOS — TINANGGAL ANG LAHAT NG PUWANG! TUMATANGGAP NA ANG KEY CODE!
        @JavascriptInterface
        fun verifyKeyCode(input: String): String {
            // ✅ TANGGALIN ANG LAHAT NG PUWANG — KAHIT SA LOOB NG KEY CODE!
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
            webView.post { webView.reload() }
        }
    }
}
