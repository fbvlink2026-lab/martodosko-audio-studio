package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {

    companion object {
        // ✅ Dito ilalagay ang bersyon sa GitHub
        private const val VERSION_URL = 
            "https://raw.githubusercontent.com/fbvlink2026-lab/martodosko-audio-studio/main/docs/version.json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Toast.makeText(this, "Martodosko Studio — Gumagana!", Toast.LENGTH_SHORT).show()
        Log.d("APP", "✅ Bumukas nang walang crash!")

        // ✅ Simulan ang pag-check ng update
        checkForUpdates()
    }

    private fun checkForUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("UPDATE", "🔍 Tinitignan kung may bagong bersyon...")

                val conn = URL(VERSION_URL).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000

                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val resp = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) resp.append(line)
                reader.close()
                conn.disconnect()

                val json = JSONObject(resp.toString())
                val latestVer = json.getString("version")
                val currentVer = packageManager.getPackageInfo(packageName, 0).versionName

                Log.d("UPDATE", "Kasalukuyan: v$currentVer  |  Pinakabago: v$latestVer")

                if (latestVer != currentVer) {
                    Log.d("UPDATE", "🔔 MAY BAGONG BERSYON! v$latestVer")
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "May bagong bersyon: v$latestVer", Toast.LENGTH_LONG).show()
                    }
                    // ✅ Dito lalabas ang dialog + download mamaya
                } else {
                    Log.d("UPDATE", "✅ Nasa pinakabago na")
                }
            } catch (e: Exception) {
                Log.e("UPDATE", "⚠️ Hindi macheck: ${e.message}")
            }
        }
    }
}
