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

        // ✅ IKABIT ANG ANIMASYON
        val rootView = findViewById<LinearLayout>(android.R.id.content)
        val anim = AnimationUtils.loadAnimation(this, R.anim.splash_anim)
        rootView.startAnimation(anim)

        // ⏰ 2.5 Segundo bago lumipat
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2500)
    }
}
