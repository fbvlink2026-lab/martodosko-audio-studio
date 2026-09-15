// ==================================================
// FILE: MainActivity.kt — ✅ FORCE CHECK UPDATE MULA SA SIDE MENU! WALANG TINANGGAL!
// VERSION: 1.0.70 — TUMUTUGMA NA SA SideMenu.kt! AUTO UPDATE NANDOON PA RIN!
// UPDATED: 2026-09-16
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {

    // ✅ SIDE MENU — SARILING FILE NA!
    private lateinit var sideMenu: SideMenu

    companion object {
        private const val VERSION_URL =
            "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
        private const val BASE_APK_URL =
            "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/"
        
        private const val PERMISSION_STORAGE = 1001
        private var downloadId: Long = -1
        private var apkFileName = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ==============================================
        // ✅ SIDE MENU — SARILING FILE NA! TAWAG LANG! — WALANG PINAGBAGO!
        // ==============================================
        sideMenu = SideMenu.setup(
            activity = this,
            drawerLayoutId = R.id.drawer_layout,
            btnOpenMenuId = R.id.btn_hamburger,
            btnCloseMenuId = R.id.btn_close_menu,
            tvVersionId = R.id.tv_version
        )

        // ==============================================
        // ✅ BAGONG DAGDAG — FORCE CHECK UPDATE MULA SA SIDE MENU!
        // ==============================================
        val forceCheck = intent?.getBooleanExtra("FORCE_CHECK_UPDATE", false) ?: false
        if (forceCheck) {
            Toast.makeText(this, "🔄 Sinusuri ang update mula sa menu...", Toast.LENGTH_SHORT).show()
            // ✅ LAGAY NA AGAD — HINDI NA HIHINTAY ANG PERMISSION CHECK
            checkForUpdates()
        } else {
            // ✅ KARANIWANG PAGBUKAS — GANOON PA RIN!
            Toast.makeText(this, "Martodosko Studio — Sinusuri...", Toast.LENGTH_SHORT).show()
            checkPermissions()
        }
    }

    // ==============================================
    // ✅ ORIHINAL NA — WALANG PINAGBAGO!
    // ==============================================
    private fun checkPermissions() {
        val neededPermissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            neededPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toTypedArray(), PERMISSION_STORAGE)
        } else {
            checkForUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_STORAGE) checkForUpdates()
    }

    // ==============================================
    // ✅ ORIHINAL NA — AUTO UPDATE! WALANG PINAGBAGO! TINITINGNAN ANG docs/version.json!
    // ==============================================
    fun checkForUpdates() { // ✅ GINAWING PUBLIC — PARA MATAWAG MULA SA SIDE MENU!
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("UPDATE", "🔍 Tinitignan ang update...")
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
                apkFileName = json.optString("apkFile", "Martodosko-Studio-v$latestVer.apk")

                @Suppress("DEPRECATION")
                val currentVer = cleanVersion(packageManager.getPackageInfo(packageName, 0).versionName)

                if (isUpdateAvailable(latestVer, currentVer)) {
                    runOnUiThread { showUpdateDialog(latestVer) }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "✅ Nasa pinakabago na — v$currentVer", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UPDATE", "⚠️ Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "⚠️ Hindi masuri ang update", Toast.LENGTH_SHORT).show()
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

    private fun showUpdateDialog(version: String) {
        AlertDialog.Builder(this)
            .setTitle("🔔 May Bagong Bersyon — v$version")
            .setMessage("Gusto mo bang i-download at i-install ang pinakabagong bersyon?\n\n⚠️ Kung lalabas ang 'Package Conflict' — burahin muna ang lumang bersyon nang isang beses lang. Mula noon, kusang mag-a-update na!")
            .setPositiveButton("✅ I-download") { _, _ -> downloadApk() }
            .setNegativeButton("❌ Mamaya na", null)
            .setCancelable(false)
            .show()
    }

    private fun downloadApk() {
        val downloadUrl = "$BASE_APK_URL$apkFileName"
        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle("Martodosko Update")
            setDescription("Nagda-download...")
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFileName)
            setMimeType("application/vnd.android.package-archive")
            allowScanningByMediaScanner()
        }
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = dm.enqueue(request)
        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        Toast.makeText(this, "📥 Nagsimula ang pag-download", Toast.LENGTH_LONG).show()
    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
            if (id == downloadId) {
                unregisterReceiver(this)
                openInstaller()
            }
        }
    }

    private fun openInstaller() {
        try {
            val apkFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), apkFileName)
            if (!apkFile.exists()) {
                Toast.makeText(this, "⚠️ Hindi mahanap ang file", Toast.LENGTH_LONG).show()
                return
            }
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "$packageName.fileprovider", apkFile)
            } else {
                Uri.fromFile(apkFile)
            }
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(installIntent)
            Toast.makeText(this, "📦 Hinihingi ang pahintulot...", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ==============================================
    // ✅ BACK PRESSED — GUMAGANA SA SIDE MENU! — WALANG PINAGBAGO!
    // ==============================================
    override fun onBackPressed() {
        if (::sideMenu.isInitialized && sideMenu.drawerLayout.isDrawerOpen(Gravity.START)) {
            sideMenu.close()
        } else {
            super.onBackPressed()
        }
    }
}
