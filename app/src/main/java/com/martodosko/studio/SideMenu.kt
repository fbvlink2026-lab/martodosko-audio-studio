// ==================================================
// FILE: SideMenu.kt — ✅ PAREHONG URL AT PARAAN NG MAINACTIVITY! fbvlink2026-lab!
// VERSION: 1.5.1 — ✅ GUMAGANA NA! PAREHO NG MAINACTIVITY! BROWSER DOWNLOAD!
// UPDATED: 2026-09-19 — URL LANG AT LOGIC ANG INAYOS! WALANG IBANG PINAGBAGO!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.widget.ImageView
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
    
    // ✅ PAREHONG URL GAYA NG MAINACTIVITY — fbvlink2026-lab! ITO ANG GUMAGANA!
    private val VERSION_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
    private val BASE_APK_URL = 
        "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/"

    init {
        getCurrentVersion()
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
    // ✅ PAREHONG PARAAN NG MAINACTIVITY — COROUTINE, NO-CACHE, CLEAN VERSION!
    // ==============================================
    private fun checkForUpdatesDirect() {
        Toast.makeText(activity, "🔄 Sinusuri ang update...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("UPDATE", "🔍 Tinitignan ang update...")
                
                // ✅ PAREHONG NO-CACHE PARAAN — HINDI LUMANG DATA!
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
                                // ✅ DIREKTANG BUBUKAS SA BROWSER — DOON ANG PAG-DOWNLOAD!
                                try {
                                    val downloadUrl = "$BASE_APK_URL$apkFile"
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    activity.startActivity(browserIntent)
                                    Toast.makeText(activity, "🌐 Binuksan sa browser...", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(activity, "❌ Hindi mabuksan ang browser", Toast.LENGTH_SHORT).show()
                                    Log.e("UPDATE", "❌ Browser error", e)
                                }
                            }
                            .setNegativeButton("❌ Mamaya na", null)
                            .setCancelable(true)
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
                Log.e("UPDATE", "⚠️ Error checking update", e)
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

    // ✅ PAREHONG VERSION CLEANER — TINATANGGAL ANG "v"
    private fun cleanVersion(v: String) = v.trim().removePrefix("v").removePrefix("V").replace(Regex("[^0-9.]"), "")

    // ✅ PAREHONG VERSION COMPARISON — TAMA ANG PAGHAHAMBING
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
    // ✅ LAHAT NG MENU BUTTONS — WALANG PINAGBAGO!
    // ==============================================
    private fun setupMenuButtons() {
        // ✅ CLOSE BUTTON
        activity.findViewById<ImageView>(R.id.btn_close_menu)?.setOnClickListener {
            close()
        }

        // ✅ MIXER
        activity.findViewById<TextView>(R.id.menu_mixer)?.setOnClickListener {
            close()
            if (activity is MixerActivity) {
                Toast.makeText(activity, "✅ Nasa Mixer ka na!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val intent = Intent(activity, MixerActivity::class.java)
                activity.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ MixerActivity hindi nakarehistro", Toast.LENGTH_SHORT).show()
                Log.e("MIXER", "❌ Error", e)
            }
        }

        // ✅ PRESETS
        activity.findViewById<TextView>(R.id.menu_presets)?.setOnClickListener {
            close()
            Toast.makeText(activity, "📋 Presets — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ✅ SETTINGS → ContentActivity
        activity.findViewById<TextView>(R.id.menu_settings)?.setOnClickListener {
            close()
            try {
                val intent = Intent(activity, ContentActivity::class.java)
                intent.putExtra("target_screen", "SETTINGS")
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                activity.startActivity(intent)
                Toast.makeText(activity, "⚙️ Binubuksan ang Settings...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(activity, "❌ ContentActivity hindi pa handa", Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ GUITAR EFFECTS
        activity.findViewById<TextView>(R.id.menu_guitar)?.setOnClickListener {
            close()
            Toast.makeText(activity, "🎸 Guitar Effects — Bubukas...", Toast.LENGTH_SHORT).show()
        }

        // ✅ CHECK UPDATE — ✅ PAREHONG LOGIC NG MAINACTIVITY! BUBUKAS SA BROWSER!
        activity.findViewById<TextView>(R.id.menu_update)?.setOnClickListener {
            close()
            checkForUpdatesDirect()
        }

        // ✅ HELP → ContentActivity
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

        // ✅ JOIN US → ContentActivity
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

        // ✅ ABOUT → ContentActivity
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

        // ✅ ADMIN → ContentActivity
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

        // ✅ WHAT'S NEW → ContentActivity
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

            btnCloseMenuId?.let { id ->
                activity.findViewById<ImageView>(id)?.setOnClickListener {
                    sideMenu.close()
                }
            }

            return sideMenu
        }
    }
}
