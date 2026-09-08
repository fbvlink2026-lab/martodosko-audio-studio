package com.martodosko.studio

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val GITHUB_VERSION_URL = "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
    private val GITHUB_APK_URL = "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/Martodosko-Studio-v"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Pagbukas — agad tignan kung may bagong bersyon
        checkForUpdate()
    }

    // ==================================================
    // ✅ AUTO-UPDATE DETECTOR
    // ==================================================
    private fun checkForUpdate() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Kunin ang pinakabagong bersyon mula sa GitHub
                val connection = URL(GITHUB_VERSION_URL).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                connection.disconnect()

                val json = JSONObject(response.toString())
                val latestVersion = json.getString("version") // "1.0.2"

                // 2. Kunin ang kasalukuyang bersyon sa telepono
                val currentVersion = packageManager.getPackageInfo(packageName, 0).versionName

                Log.d("UPDATE", "Kasalukuyan: $currentVersion | Pinakabago: $latestVersion")

                // 3. Ihambing — kung mas bago — ipakita ang mensahe
                if (isNewerVersion(latestVersion, currentVersion)) {
                    withContext(Dispatchers.Main) {
                        showUpdateDialog(latestVersion, currentVersion)
                    }
                }

            } catch (e: Exception) {
                Log.e("UPDATE", "Hindi macheck ang update", e)
            }
        }
    }

    // ✅ Ihambing ang dalawang numero ng bersyon
    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").map { it.toInt() }
        val currentParts = current.split(".").map { it.toInt() }

        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val l = if (i < latestParts.size) latestParts[i] else 0
            val c = if (i < currentParts.size) currentParts[i] else 0
            if (l > c) return true
            if (l < c) return false
        }
        return false // Pareho lang — walang bago
    }

    // ✅ Ipakita ang mensahe — humingi ng pahintulot
    private fun showUpdateDialog(latest: String, current: String) {
        AlertDialog.Builder(this)
            .setTitle("🔔 May Bagong Bersyon!")
            .setMessage("Naka-install: v$current\nAvailable: v$latest\n\nGusto mo bang i-update ngayon?")
            .setPositiveButton("✅ I-Update") { _, _ ->
                downloadAndInstallApk(latest)
            }
            .setNegativeButton("⏳ Mamaya Na", null)
            .setCancelable(true)
            .show()
    }

    // ✅ I-download at i-install ang bagong APK
    private fun downloadAndInstallApk(version: String) {
        val apkUrl = "$GITHUB_APK_URL$version.apk"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(apkUrl))
        startActivity(intent)

        // 💡 Pagkatapos i-download — ang browser o file manager ang magtatanong:
        // "I-install ang aplikasyon?" → Pahintulutan → I-install
    }
}
