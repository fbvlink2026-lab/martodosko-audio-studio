// ==================================================
// FILE: SideMenu.kt — ✅ DOWNLOAD → DIREKTANG BUBUKAS SA BROWSER!
// VERSION: 1.4.1 — ✅ I-CLICK = BUBUKAS SA BROWSER! WALANG IBANG PINAGBAGO!
// UPDATED: 2026-09-19 — WALANG TINANGGAL, DAGDAG LANG!
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
    
    // ✅ KONFIGURASYON — DIREKTA MULA SA GITHUB WEBSITE
    private val UPDATE_JSON_URL = "https://martodosko.github.io/martodosko-audio-studio/version.json"
    private val GITHUB_REPO_URL = "https://github.com/martodosko/martodosko-audio-studio"

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
    // ✅ SARILING UPDATE CHECKER — DIREKTA MULA SA SITE!
    // ✅ PAG PININDOT ANG DOWNLOAD → BUBUKAS SA BROWSER!
    // ==============================================
    private fun checkForUpdatesDirect() {
        Toast.makeText(activity, "🔄 Sinusuri ang update...", Toast.LENGTH_SHORT).show()

        Thread {
            try {
                val url = URL(UPDATE_JSON_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                if (connection.responseCode != 200) {
                    throw Exception("HTTP ${connection.responseCode}")
                }

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val jsonText = reader.readText()
                reader.close()
                connection.disconnect()

                val json = JSONObject(jsonText)
                val latestVer = json.optString("version", "0.0.0")
                val apkFile = json.optString("apkFile", "")
                val releaseDate = json.optString("released", "Unknown")
                
                // ✅ BUUIN ANG DOWNLOAD URL — DIREKTA SA APK O SA RELEASE PAGE
                val downloadUrl = when {
                    apkFile.startsWith("http") -> apkFile
                    apkFile.isNotEmpty() -> "$GITHUB_REPO_URL/releases/download/v$latestVer/$apkFile"
                    else -> "$GITHUB_REPO_URL/releases"
                }

                val isNewer = isVersionNewer(currentVer, latestVer)

                Handler(Looper.getMainLooper()).post {
                    if (isNewer) {
                        AlertDialog.Builder(activity)
                            .setTitle("✅ May Bagong Bersyon!")
                            .setMessage("Kasalukuyan: v$currentVer\nPinakabago: v$latestVer\nPetsa: $releaseDate\n\nBubukas sa browser ang pag-download...")
                            .setPositiveButton("⬇️ I-download") { _, _ ->
                                // ✅ BUBUKAS SA DEFAULT BROWSER — DOON NA MAAYOS ANG PAG-DOWNLOAD!
                                try {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    activity.startActivity(browserIntent)
                                    Toast.makeText(activity, "🌐 Binuksan sa browser...", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(activity, "❌ Hindi mabuksan ang browser", Toast.LENGTH_SHORT).show()
                                    Log.e("UPDATE", "❌ Browser error", e)
                                }
                            }
                            .setNegativeButton("❌ Mamaya", null)
                            .setCancelable(true)
                            .show()
                    } else {
                        AlertDialog.Builder(activity)
                            .setTitle("✅ Nasa Pinakabagong Bersyon")
                            .setMessage("Kasalukuyan: v$currentVer\nIkaw ay napapanahon na!")
                            .setPositiveButton("✅ Sige", null)
                            .show()
                    }
                }

            } catch (e: Exception) {
                Log.e("UPDATE_CHECK", "❌ Error checking update", e)
                Handler(Looper.getMainLooper()).post {
                    AlertDialog.Builder(activity)
                        .setTitle("⚠️ Hindi Masuri ang Update")
                        .setMessage("${e.message}\n\nSiguraduhing may internet connection.")
                        .setPositiveButton("✅ Sige", null)
                        .show()
                }
            }
        }.start()
    }

    // ✅ IHAMBING ANG BERSYON
    private fun isVersionNewer(current: String, latest: String): Boolean {
        return try {
            val currParts = current.split(".").map { it.toIntOrNull() ?: 0 }
            val lateParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
            
            for (i in 0 until maxOf(currParts.size, lateParts.size)) {
                val c = if (i < currParts.size) currParts[i] else 0
                val l = if (i < lateParts.size) lateParts[i] else 0
                if (l > c) return true
                if (l < c) return false
            }
            false
        } catch (e: Exception) {
            latest != current
        }
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
                val fullError = when {
                    e.message?.contains("Activity class not found") == true ->
                        "❌ MixerActivity hindi nakarehistro sa AndroidManifest.xml"
                    else -> "❌ ${e.javaClass.simpleName}: ${e.message}"
                }
                Toast.makeText(activity, fullError, Toast.LENGTH_LONG).show()
                Log.e("MIXER", "❌ $fullError", e)
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

        // ✅ CHECK UPDATE → DIREKTA MULA SA SITE, BUBUKAS SA BROWSER!
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
