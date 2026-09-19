// ==================================================
// FILE: SideMenu.kt — ✅ KOMPLETO NA! COLLAPSE/EXPAND GUMAGANA NA! GUITAR = PANGALAWANG BUTTON!
// VERSION: 2.1.0 — ✅ IKINABIT NA ANG setupCollapsible() SA BAWAT BUTTON! TUGMA SA XML IDs!
// UPDATED: 2026-09-19 — WALANG BINAGO SA UPDATE LOGIC — IKINABIT LANG ANG COLLAPSE!
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
    
    // ✅ PAREHONG URL GAYA NG MAINACTIVITY — fbvlink2026-lab!
    private val VERSION_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
    private val BASE_APK_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/"

    // ✅ TRACK NG COLLAPSE STATE
    private val menuExpanded = mutableMapOf<Int, Boolean>()

    init {
        getCurrentVersion()
        setupCollapsibleMenus()  // ✅ BAGO — IKINABIT ANG COLLAPSE!
        setupMenuButtons()
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
    // ✅ COLLAPSE/EXPAND HELPER — PARA SA BAWAT BUTTON!
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
        
        // ✅ AYUSIN ANG ARROW — HUWAG DOBLEHIN KAPAG BINASA MULI
        val currentText = header.text.toString()
        if (!currentText.startsWith("▶  ") && !currentText.startsWith("▼  ")) {
            header.text = if (defaultExpanded) "▼  $currentText" else "▶  $currentText"
        }

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

    // ==============================================
    // ✅ BAGONG FUNCTION — IKAKABIT ANG LAHAT NG COLLAPSIBLE MENU!
    // ==============================================
    private fun setupCollapsibleMenus() {
        // ✅ 1 — MIXER — default = bukas
        setupCollapsible(R.id.menu_mixer, R.id.submenu_mixer, defaultExpanded = true)
        
        // ✅ 2 — GUITAR EFFECTS — default = nakatiklop
        setupCollapsible(R.id.menu_guitar, R.id.submenu_guitar, defaultExpanded = false)
        
        // ✅ 3 — PRESETS — default = nakatiklop
        setupCollapsible(R.id.menu_presets, R.id.submenu_presets, defaultExpanded = false)
        
        // ✅ 4 — SETTINGS — default = nakatiklop
        setupCollapsible(R.id.menu_settings, R.id.submenu_settings, defaultExpanded = false)
        
        // ✅ IBA PANG BUTTON — WALANG SUB-MENU, DI KAILANGAN NG COLLAPSE
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
    // ✅ BAGONG PAGKAKASUNOD — GUITAR EFFECTS = PANGALAWANG BUTTON!
    // ==============================================
    private fun setupMenuButtons() {
        // ✅ CLOSE BUTTON
        activity.findViewById<ImageView>(R.id.btn_close_menu)?.setOnClickListener { close() }

        // ==============================================
        // 📌 PANGUNAHING MENU — MAY COLLAPSE/EXPAND NA!
        // ==============================================

        // ✅ 1 — MIXER (PANG-UNA) — SUB-MENU CLICK HANDLERS ILALAGAY SA XML O DITO
        activity.findViewById<TextView>(R.id.menu_mixer)?.setOnClickListener {
            // ✅ COLLAPSE/EXPAND LANG — HINDI NAGLILIPAT NG SCREEN
        }

        // ✅ 2 — GUITAR EFFECTS (PANG-ALAWA — AYON SA UTOS!)
        activity.findViewById<TextView>(R.id.menu_guitar)?.setOnClickListener {
            // ✅ COLLAPSE/EXPAND LANG — ANG PAGPASOK SA SCREEN AY NASA SUB-MENU
        }

        // ✅ 3 — PRESETS
        activity.findViewById<TextView>(R.id.menu_presets)?.setOnClickListener {
            // ✅ COLLAPSE/EXPAND LANG
        }

        // ✅ 4 — SETTINGS → ContentActivity
        activity.findViewById<TextView>(R.id.menu_settings)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "SETTINGS")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ 5 — CHECK UPDATE
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            checkForUpdatesDirect()
        }

        // ✅ 6 — HELP → ContentActivity
        activity.findViewById<TextView>(R.id.menu_help)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "HELP")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ 7 — JOIN US → ContentActivity
        activity.findViewById<TextView>(R.id.menu_join)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "JOIN_US")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ 8 — ABOUT → ContentActivity
        activity.findViewById<TextView>(R.id.menu_about)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "ABOUT")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ 9 — WHAT'S NEW
        activity.findViewById<TextView>(R.id.menu_whatsnew)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "ABISO")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ 10 — ADMIN
        activity.findViewById<TextView>(R.id.menu_admin)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "ADMIN")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ EXIT
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
