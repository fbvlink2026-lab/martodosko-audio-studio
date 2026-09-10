package com.martodosko.studio

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
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

    companion object {
        private const val VERSION_URL =
            "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
        private const val BASE_APK_URL =
            "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/"
        private var downloadId: Long = -1
    }

    private fun cleanVersion(v: String): String {
        return v.trim().removePrefix("v").removePrefix("V").replace(Regex("[^0-9.]"), "")
    }

    private fun shouldUpdate(latest: String, current: String): Boolean {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this, "Martodosko Studio — Sinusuri...", Toast.LENGTH_SHORT).show()
        registerDownloadReceiver()
        checkForUpdates()
    }

    private fun checkForUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "$VERSION_URL?t=${System.currentTimeMillis()}"
                val conn = URL(url).openConnection() as HttpURLConnection
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
                val apkFileName = json.optString("apkFile", "Martodosko-Studio-v$latestVer.apk")

                @Suppress("DEPRECATION")
                val currentVer = cleanVersion(packageManager.getPackageInfo(packageName, 0).versionName)

                Log.d("UPDATE", "GitHub: v$latestVer | Naka-install: v$currentVer")

                if (shouldUpdate(latestVer, currentVer)) {
                    runOnUiThread { showUpdateDialog(latestVer, apkFileName) }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "✅ Nasa pinakabago na — v$currentVer", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("UPDATE", "Error: ${e.message}")
            }
        }
    }

    private fun showUpdateDialog(version: String, apkFile: String) {
        AlertDialog.Builder(this)
            .setTitle("🔔 May Bagong Bersyon — v$version")
            .setMessage("Gusto mo bang i-download at i-install ang pinakabagong bersyon?")
            .setPositiveButton("✅ I-download") { _, _ ->
                downloadApk(apkFile)
            }
            .setNegativeButton("❌ Mamaya na", null)
            .setCancelable(false)
            .show()
    }

    private fun downloadApk(apkFile: String) {
        val downloadUrl = "$BASE_APK_URL$apkFile"
        Log.d("UPDATE", "Nagda-download: $downloadUrl")

        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle("Martodosko Update")
            setDescription("Nagda-download ang bersyon...")
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFile)
            setMimeType("application/vnd.android.package-archive")
        }

        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = dm.enqueue(request)

        Toast.makeText(this, "📥 Nagsimula ang pag-download — tignan ang abiso", Toast.LENGTH_LONG).show()
    }

    private fun registerDownloadReceiver() {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
                if (id == downloadId) {
                    Log.d("UPDATE", "Tapos na ang pag-download — magbubukas ng installer")
                    openInstaller()
                }
            }
        }
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun openInstaller() {
        try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val apkFile = downloadDir.listFiles()?.maxByOrNull { it.lastModified() }
            if (apkFile != null && apkFile.name.endsWith(".apk")) {
                val uri = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    androidx.core.content.FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",
                        apkFile
                    )
                } else {
                    Uri.fromFile(apkFile)
                }

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(installIntent)
                Toast.makeText(this, "📦 Hinihingi ang pahintulot sa pag-install...", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("UPDATE", "Install error: ${e.message}")
            Toast.makeText(this, "⚠️ Hindi mabuksan ang installer: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
