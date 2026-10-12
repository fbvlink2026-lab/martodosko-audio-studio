// ==================================================
// FILE: MartodoskoApp.kt
// VERSION: v2.1.0-monolith-final
// PURPOSE: Minimal Application Class for Hilt Init
// NOTE: Stripped down to bare minimum to isolate crashes
// ==================================================
package com.martodosko.studio

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MartodoskoApp : Application() {
    // Empty intentionally. 
    // If this crashes, the issue is strictly with Hilt/Dagger setup or Manifest registration.
}
