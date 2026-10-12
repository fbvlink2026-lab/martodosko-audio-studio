// ==================================================
// FILE: MartodoskoApp.kt
// VERSION: 2.1.0 - DEBUG MODE ENABLED
// PURPOSE: Application Class with Global Crash Handler
// NOTE: Shows Toast on crash to identify error without Logcat
// ==================================================
package com.martodosko.studio

import android.app.Application
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp
import java.io.PrintWriter
import java.io.StringWriter

@HiltAndroidApp
class MartodoskoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // ✅ SET GLOBAL CRASH HANDLER TO SHOW TOAST ON FAILURE
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                throwable.printStackTrace(pw)
                val stackTrace = sw.toString().take(500) // Limit length for Toast
                
                // Show Toast on Main Thread
                runOnUiThread {
                    Toast.makeText(
                        applicationContext, 
                        "💥 CRASH:\n${throwable.message}\n\nStack:\n$stackTrace", 
                        Toast.LENGTH_LONG
                    ).show()
                }
                
                // Wait briefly so user can read the toast before process dies
                Thread.sleep(3000) 
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Kill the process after showing error
                System.exit(1)
            }
        }

        println("✅ [DEBUG] MartodoskoApp initialized")
    }
}
