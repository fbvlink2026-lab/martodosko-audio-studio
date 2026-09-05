package com.martodosko.studio
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer = findViewById(R.id.drawer_layout)
        findViewById<View>(R.id.btn_menu).setOnClickListener { drawer.openDrawer(GravityCompat.START) }
        findViewById<View>(R.id.btn_close).setOnClickListener { drawer.closeDrawers() }
        findViewById<View>(R.id.nav_mixer).setOnClickListener {
            startActivity(Intent(this, MixerActivity::class.java))
            drawer.closeDrawers()
        }
        findViewById<View>(R.id.nav_effects).setOnClickListener {
            startActivity(Intent(this, EffectsActivity::class.java))
            drawer.closeDrawers()
        }
    }
}
