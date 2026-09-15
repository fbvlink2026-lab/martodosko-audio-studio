// ==================================================
// FILE: MixerActivity.kt — ✅ MAY SIDE MENU NA! TAWAG LANG SA SideMenu!
// VERSION: 2.0.0 — HINDI NA ULIT-ULITIN ANG SIDE MENU CODE!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.widget.Toast

class MixerActivity : Activity() {

    private lateinit var sideMenu: SideMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ SIDE MENU — ISANG LINYA LANG! TAPOS NA! PAREHO SA MAINACTIVITY!
            sideMenu = SideMenu.setup(
                activity = this,
                drawerLayoutId = R.id.drawer_layout,
                btnMenuId = R.id.btn_menu
            )

            // ✅ GAIN KNOB — PERFECT NA!
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onBackPressed() {
        if (::sideMenu.isInitialized && sideMenu.drawerLayout.isDrawerOpen(android.view.Gravity.START)) {
            sideMenu.close()
        } else {
            super.onBackPressed()
        }
    }
}
