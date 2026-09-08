package com.martodosko.studio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.LinearLayout

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // ✅ TAMA NA — TUMUTUKOY SA SARILING LAYOUT MO
        val rootLayout = findViewById<LinearLayout>(R.id.splash_root)
        val animation = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        rootLayout.startAnimation(animation)

        // ⏰ Lumipat pagkatapos
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
