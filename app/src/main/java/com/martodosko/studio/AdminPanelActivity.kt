// ==================================================
// FILE: AdminPanelActivity.kt — ✅ INAYOS NA ANG FILE EDITOR CHECK! BUBUKAS NA!
// VERSION: 5.1.3 — ✅ HINDI NA HINAHARANG! MAY ENCRYPTED TOKEN PA LANG = PWEDI NA!
// UPDATED: 2026-09-21 — PALIT LANG ANG LOGIC NG btn_admin_file_editor!
// ==================================================
package com.martodosko.studio

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import java.security.KeyStore

class AdminPanelActivity : FragmentActivity() {

    private lateinit var sideMenu: SideMenu
    private lateinit var prefs: SharedPreferences
    private lateinit var githubPrefs: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout

    private var currentUserLevel: String = "GUEST"
    private var isSessionActive: Boolean = false

    // ==============================================
    // 🔑 NAKA-EMBED NA — DECRYPTED ONCE, IBABAHAGI SA LAHAT!
    // ==============================================
    var decryptedGithubToken: String? = null
        private set
    private var repoOwner: String = ""
        private set
    private var repoName: String = ""
        private set
    var isGithubVerified: Boolean = false
        private set

    private val KEY_ALIAS = "martodosko_github_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        prefs = getSharedPreferences("admin_session", Context.MODE_PRIVATE)
        githubPrefs = getSharedPreferences("github_prefs", Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawer_layout)

        isSessionActive = prefs.getBoolean("session_active", false)
        currentUserLevel = prefs.getString("user_level", "GUEST") ?: "GUEST"

        loadAndDecryptGithubConfig()

        sideMenu = SideMenu.setup(
            activity = this,
            drawerLayoutId = R.id.drawer_layout,
            btnOpenMenuId = R.id.btn_hamburger,
            btnCloseMenuId = R.id.btn_close_menu,
            tvVersionId = R.id.tv_version
        )

        checkAccessLevel()
        setupAdminSideMenu()

        if (savedInstanceState == null) {
            showFragment(AdminHomeFragment())
        }
    }

    private fun loadAndDecryptGithubConfig() {
        repoOwner = githubPrefs.getString("repo_owner", "") ?: ""
        repoName = githubPrefs.getString("repo_name", "") ?: ""
        isGithubVerified = githubPrefs.getBoolean("token_verified", false)

        val encryptedToken = githubPrefs.getString("encrypted_github_token", null)
        if (!encryptedToken.isNullOrEmpty()) {
            try {
                decryptedGithubToken = decryptData(encryptedToken)
            } catch (e: Exception) {
                decryptedGithubToken = null
            }
        }
    }

    private fun decryptData(encryptedText: String): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        val secretKey = entry.secretKey

        val combined = Base64.decode(encryptedText, Base64.DEFAULT)
        val ivSize = 12
        val iv = combined.copyOfRange(0, ivSize)
        val data = combined.copyOfRange(ivSize, combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val decrypted = cipher.doFinal(data)
        return String(decrypted, Charsets.UTF_8)
    }

    // ==============================================
    // ✅ PUBLIC GETTERS
    // ==============================================
    fun getGithubToken(): String? = decryptedGithubToken
    fun getRepoOwner(): String = repoOwner
    fun getRepoName(): String = repoName
    fun isGithubReady(): Boolean = !decryptedGithubToken.isNullOrEmpty() && repoOwner.isNotEmpty() && repoName.isNotEmpty()

    private fun setupAdminSideMenu() {
        findViewById<TextView>(R.id.btn_admin_menu)?.setOnClickListener {
            drawerLayout.openDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_close_admin_menu)?.setOnClickListener {
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_admin_home)?.setOnClickListener {
            showFragment(AdminHomeFragment())
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_admin_keys)?.setOnClickListener {
            if (currentUserLevel == "OWNER") {
                showFragment(KeyGeneratorFragment())
            } else {
                Toast.makeText(this@AdminPanelActivity, "👑 OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_admin_github_token)?.setOnClickListener {
            if (currentUserLevel == "OWNER") {
                showFragment(GithubManagerFragment())
            } else {
                Toast.makeText(this@AdminPanelActivity, "👑 OWNER lang ang makapag-setup ng GitHub Token!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        // ==============================================
        // ✅ INAYOS NA — FILE EDITOR BUTTON! BUBUKAS NA!
        // ==============================================
        findViewById<TextView>(R.id.btn_admin_file_editor)?.setOnClickListener {
            if (currentUserLevel == "OWNER" || currentUserLevel == "ADMIN") {
                // ✅ HUWAG HINTAYIN ANG DECRYPT — MAY ENCRYPTED TOKEN + OWNER + REPO = PWEDI NA!
                val hasEncryptedToken = githubPrefs.getString("encrypted_github_token", null) != null
                val hasOwner = githubPrefs.getString("repo_owner", "")?.isNotEmpty() == true
                val hasRepo = githubPrefs.getString("repo_name", "")?.isNotEmpty() == true

                if (!hasEncryptedToken || !hasOwner || !hasRepo) {
                    Toast.makeText(this@AdminPanelActivity, "⚠️ I-setup muna ang GitHub Token bago gamitin ang File Editor!", Toast.LENGTH_LONG).show()
                    showFragment(GithubManagerFragment())
                } else {
                    // ✅ BUBUKAS NA! WALANG HARANG!
                    showFragment(FileEditorFragment())
                }
            } else {
                Toast.makeText(this@AdminPanelActivity, "🔐 ADMIN o OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_admin_presets)?.setOnClickListener {
            if (currentUserLevel == "OWNER" || currentUserLevel == "ADMIN") {
                showFragment(PresetModerationFragment())
            } else {
                Toast.makeText(this@AdminPanelActivity, "🔐 ADMIN o OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_admin_users)?.setOnClickListener {
            if (currentUserLevel == "OWNER" || currentUserLevel == "ADMIN") {
                showFragment(UserManagementFragment())
            } else {
                Toast.makeText(this@AdminPanelActivity, "🔐 ADMIN o OWNER lang ang makakagamit nito!", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_admin_stats)?.setOnClickListener {
            showFragment(StatisticsFragment())
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
        }

        findViewById<TextView>(R.id.btn_admin_logout)?.setOnClickListener {
            prefs.edit()
                .remove("user_level")
                .remove("active_member_key")
                .putBoolean("session_active", false)
                .apply()

            decryptedGithubToken = null
            repoOwner = ""
            repoName = ""

            Toast.makeText(this@AdminPanelActivity, "✅ Naka-logout na. Burado ang Token sa memory.", Toast.LENGTH_SHORT).show()
            drawerLayout.closeDrawer(findViewById<LinearLayout>(R.id.drawer_admin))
            finish()
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.admin_content_container, fragment)
            .commit()
    }

    private fun checkAccessLevel() {
        val accessTitle = findViewById<TextView>(R.id.admin_access_level)

        if (!isSessionActive) {
            accessTitle.text = "❌ WALANG AKTIBONG SESSION"
            accessTitle.setTextColor(0xFFFF5252.toInt())
            Toast.makeText(this@AdminPanelActivity, "❌ Mag-log in muna.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        when (currentUserLevel) {
            "OWNER" -> {
                accessTitle.text = "👑 OWNER — BUONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFFD700.toInt())
            }
            "ADMIN" -> {
                accessTitle.text = "🔐 ADMIN — LIMITADONG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF9800.toInt())
            }
            else -> {
                accessTitle.text = "❌ WALANG KAPANGYARIHAN"
                accessTitle.setTextColor(0xFFFF5252.toInt())
                Toast.makeText(this@AdminPanelActivity, "❌ Hindi sapat ang antas ng iyong Key.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (decryptedGithubToken.isNullOrEmpty()) {
            loadAndDecryptGithubConfig()
        }
    }
}
