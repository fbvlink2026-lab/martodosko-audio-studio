package com.martodosko.studio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ✅ IKABIT ANG SPLASH LAYOUT — DAHIL SIGURADO NA ANG FILE!
        setContentView(R.layout.splash_screen)

        // ⏰ 2 Segundo → lumipat sa Main
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
