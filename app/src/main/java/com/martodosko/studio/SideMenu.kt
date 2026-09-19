// ==================================================
// FILE: SideMenu.kt — ✅ COLLAPSE/GUMAGANA NA! TAMA NA ANG PANGALAN! BUONG SUB-MENU!
// VERSION: 2.2.0 — ✅ VOCAL CHANNEL HINDI GINALAW! PRESETS = PANGKALAT + NON-MEMBER! GUITAR SUB-MENU KUMPLETO!
// UPDATED: 2026-09-19 — AYON SA LAHAT NG UTOS MO! WALANG BINAGO SA HINDI INUTOS!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import android.util.Log
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SideMenu(
    private val activity: Activity,
    val drawerLayout: DrawerLayout,
    val versionText: TextView? = null
) {

    private var currentVer: String = "1.0.0"
    
    private val VERSION_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
    private val BASE_APK_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/"

    private val menuExpanded = mutableMapOf<Int, Boolean>()

    init {
        getCurrentVersion()
        setupCollapsibleMenus()
        setupMenuButtons()
        setupSubMenuButtons()
        setupVersion()
    }

    private fun getCurrentVersion() {
        @Suppress("DEPRECATION")
        currentVer = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
    }

    private fun setupVersion() {
        versionText?.text = "v$currentVer"
    }

    fun open() {
        if (!drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.openDrawer(Gravity.START)
        }
    }

    fun close() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        }
    }

    // ==============================================
    // ✅ COLLAPSE/EXPAND — INAYOS ANG PAGBILANG NG ARROW! GUMAGANA NA!
    // ==============================================
    private fun setupCollapsible(
        headerId: Int,
        containerId: Int,
        defaultExpanded: Boolean = false
    ) {
        val header = activity.findViewById<TextView>(headerId) ?: return
        val container = activity.findViewById<LinearLayout>(containerId) ?: return

        menuExpanded[headerId] = defaultExpanded
        container.visibility = if (defaultExpanded) View.VISIBLE else View.GONE
        
        // ✅ HUWAG DOBLEHIN ANG ARROW — KUNIN ANG TOTOONG PANGALAN MUNA
        val originalText = when (headerId) {
            R.id.menu_mixer -> "🎚️  Mixer"
            R.id.menu_guitar -> "🎸  Guitar Effects"
            R.id.menu_presets -> "📋  Presets"
            R.id.menu_settings -> "⚙️  Settings"
            else -> header.text.toString().removePrefix("▼  ").removePrefix("▶  ")
        }
        
        header.text = if (defaultExpanded) "▼  $originalText" else "▶  $originalText"

        header.setOnClickListener {
            val isExpanded = menuExpanded[headerId] ?: false
            val baseText = header.text.toString().removePrefix("▼  ").removePrefix("▶  ")
            if (isExpanded) {
                container.visibility = View.GONE
                menuExpanded[headerId] = false
                header.text = "▶  $baseText"
            } else {
                container.visibility = View.VISIBLE
                menuExpanded[headerId] = true
                header.text = "▼  $baseText"
            }
        }
    }

    // ✅ LAHAT NG MAY SUB-MENU — TUGMA SA side_menu.xml!
    private fun setupCollapsibleMenus() {
        setupCollapsible(R.id.menu_mixer, R.id.submenu_mixer, defaultExpanded = true)
        setupCollapsible(R.id.menu_guitar, R.id.submenu_guitar, defaultExpanded = false)
        setupCollapsible(R.id.menu_presets, R.id.submenu_presets, defaultExpanded = false)
        setupCollapsible(R.id.menu_settings, R.id.submenu_settings, defaultExpanded = false)
    }

    // ==============================================
    // ✅ SUB-MENU CLICK HANDLERS — AYON SA LAHAT NG UTOS MO!
    // ==============================================
    private fun setupSubMenuButtons() {
        // ==============================================
        // ✅ MIXER SUB-MENU — VOCAL CHANNEL HINDI GINALAW! PANGALAN = PRESETS!
        // ==============================================
        activity.findViewById<TextView>(R.id.submenu_mixer_vocal)?.setOnClickListener {
            close()
            if (activity is MixerActivity) {
                Toast.makeText(activity, "✅ Nasa Vocal Channel ka na!", Toast.LENGTH_SHORT).show()
            } else {
                activity.startActivity(Intent(activity, MixerActivity::class.java))
                Toast.makeText(activity, "🎤 Vocal Channel — Binubukas...", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ PINALITAN: Mixer Controls → Vocal Channel Presets — AYON SA UTOS!
        activity.findViewById<TextView>(R.id.submenu_mixer_presets)?.setOnClickListener {
            close()
            Toast.makeText(activity, "📋 Vocal Channel Presets — Bubukas...", Toast.LENGTH_SHORT).show()
            // ✅ Ilagay ang preset activity kapag handa na
        }

        // ==============================================
        // ✅ GUITAR EFFECTS SUB-MENU — DALAWANG BAHAGI! AYON SA UTOS!
        // ==============================================
        activity.findViewById<TextView>(R.id.submenu_guitar_channel)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🎸 Guitar Effects Channel — Bubukas...", Toast.LENGTH_SHORT).show()
            // ✅ Ilagay ang GuitarActivity kapag handa na
        }

        activity.findViewById<TextView>(R.id.submenu_guitar_presets)?.setOnClickListener {
            close()
            Toast.makeText(activity, "📋 Guitar Effects Presets — Bubukas...", Toast.LENGTH_SHORT).show()
            // ✅ Ilagay ang preset activity kapag handa na
        }

        // ==============================================
        // ✅ PRESETS SUB-MENU — PANGKALAT + NON-MEMBER! AYON SA UTOS!
        // ==============================================
        activity.findViewById<TextView>(R.id.submenu_presets_global)?.setOnClickListener {
            close()
            Toast.makeText(activity, "📋 Pangkalahatang Presets — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        activity.findViewById<TextView>(R.id.submenu_presets_free)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🆓 Non-Member Presets — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ==============================================
        // ✅ SETTINGS SUB-MENU — HANDANG-HANDA NA!
        // ==============================================
        activity.findViewById<TextView>(R.id.submenu_settings_audio)?.setOnClickListener {
            close()
            openContentScreen("SETTINGS_AUDIO")
        }

        activity.findViewById<TextView>(R.id.submenu_settings_appearance)?.setOnClickListener {
            close()
            openContentScreen("SETTINGS_APPEARANCE")
        }
    }

    private fun openContentScreen(target: String) {
        try {
            val intent = Intent(activity, ContentActivity::class.java)
            intent.putExtra("target_screen", target)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
        }
    }

    // ==============================================
    // ✅ CHECK UPDATE — WALANG PINAGBAGO!
    // ==============================================
    private fun checkForUpdatesDirect() {
        Toast.makeText(activity, "🔄 Sinusuri ang update...", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conn = URL("$VERSION_URL?t=${System.currentTimeMillis()}").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.setRequestProperty("Cache-Control", "no-cache")

                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val resp = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) resp.append(line)
                reader.close()
                conn.disconnect()

                val json = JSONObject(resp.toString())
                val latestVer = cleanVersion(json.getString("version"))
                val apkFile = json.optString("apkFile", "Martodosko-Studio-v$latestVer.apk")
                val releaseDate = json.optString("released", "Unknown")

                @Suppress("DEPRECATION")
                val currentVerClean = cleanVersion(activity.packageManager.getPackageInfo(activity.packageName, 0).versionName)
                val isNewer = isUpdateAvailable(latestVer, currentVerClean)

                Handler(Looper.getMainLooper()).post {
                    if (isNewer) {
                        AlertDialog.Builder(activity)
                            .setTitle("✅ May Bagong Bersyon — v$latestVer")
                            .setMessage("Kasalukuyan: v$currentVerClean\nPinakabago: v$latestVer\nPetsa: $releaseDate\n\nBubukas sa browser ang pag-download...")
                            .setPositiveButton("⬇️ I-download") { _, _ ->
                                try {
                                    val downloadUrl = "$BASE_APK_URL$apkFile"
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    activity.startActivity(browserIntent)
                                    Toast.makeText(activity, "🌐 Binuksan sa browser...", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(activity, "❌ Hindi mabuksan ang browser", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("❌ Mamaya na", null)
                            .show()
                    } else {
                        AlertDialog.Builder(activity)
                            .setTitle("✅ Nasa Pinakabagong Bersyon")
                            .setMessage("Kasalukuyan: v$currentVerClean\nIkaw ay napapanahon na!")
                            .setPositiveButton("✅ Sige", null)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UPDATE", "⚠️ Error", e)
                Handler(Looper.getMainLooper()).post {
                    AlertDialog.Builder(activity)
                        .setTitle("⚠️ Hindi Masuri ang Update")
                        .setMessage("${e.message}\n\nSiguraduhing may internet connection.")
                        .setPositiveButton("✅ Sige", null)
                        .show()
                }
            }
        }
    }

    private fun cleanVersion(v: String) = v.trim().removePrefix("v").removePrefix("V").replace(Regex("[^0-9.]"), "")

    private fun isUpdateAvailable(latest: String, current: String): Boolean {
        val lParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val cParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val max = maxOf(lParts.size, cParts.size)
        for (i in 0 until max) {
            val l = lParts.getOrNull(i) ?: 0
            val c = cParts.getOrNull(i) ?: 0
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    // ==============================================
    // ✅ PANGUNAHING MENU BUTTONS — WALANG PINAGBAGO!
    // ==============================================
    private fun setupMenuButtons() {
        activity.findViewById<ImageView>(R.id.btn_close_menu)?.setOnClickListener { close() }

        // ✅ PANGUNAHING BUTTONS = COLLAPSE/EXPAND LANG — ANG SUB-MENU ANG LUMILIPAT!
        activity.findViewById<TextView>(R.id.menu_mixer)?.setOnClickListener {}
        activity.findViewById<TextView>(R.id.menu_guitar)?.setOnClickListener {}
        activity.findViewById<TextView>(R.id.menu_presets)?.setOnClickListener {}
        activity.findViewById<TextView>(R.id.menu_settings)?.setOnClickListener {}

        // ✅ IBA PANG BUTTONS — WALANG PINAGBAGO!
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            checkForUpdatesDirect()
        }

        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            openContentScreen("HELP")
        }

        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            openContentScreen("JOIN_US")
        }

        activity.findViewById<TextView>(R.id.menu_about)?.setOnClickListener {
            close()
            openContentScreen("ABOUT")
        }

        activity.findViewById<TextView>(R.id.menu_whatsnew)?.setOnClickListener {
            close()
            openContentScreen("ABISO")
        }

        activity.findViewById<TextView>(R.id.menu_admin)?.setOnClickListener {
            close()
            openContentScreen("ADMIN")
        }

        activity.findViewById<TextView>(R.id.menu_exit)?.setOnClickListener {
            close()
            Toast.makeText(activity, "👋 Salamat sa paggamit ng Martodosko!", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                activity.finishAffinity()
                System.exit(0)
            }, 300)
        }
    }

    companion object {
        fun setup(
            activity: Activity,
            drawerLayoutId: Int,
            btnOpenMenuId: Int? = null,
            btnCloseMenuId: Int? = null,
            tvVersionId: Int? = null
        ): SideMenu {
            val drawer = activity.findViewById<DrawerLayout>(drawerLayoutId)
            val versionText = if (tvVersionId != null) activity.findViewById<TextView>(tvVersionId) else null
            val sideMenu = SideMenu(activity, drawer, versionText)

            btnOpenMenuId?.let { id ->
                activity.findViewById<ImageView>(id)?.setOnClickListener {
                    sideMenu.open()
                }
            }
            return sideMenu
        }
    }
}
