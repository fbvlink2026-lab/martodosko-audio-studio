// ==================================================
// FILE: MartodoskoApp.kt
// VERSION: 2.1.0
// PURPOSE: Application Class with Hilt Initialization
// ==================================================
package com.martodosko.studio

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MartodoskoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Timber or other global logs here if needed
        println("✅ Martodosko Audio Studio v2.1 Initialized")
    }
}
