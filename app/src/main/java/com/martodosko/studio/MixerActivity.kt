// ==================================================
// FILE: MixerActivity.kt — ✅ ITINUGMA NA SA SideMenu! TAMA NA ANG MGA ID!
// VERSION: 2.0.1 — btn_hamburger + btn_close_menu! TUGMA SA XML!
// UPDATED: 2026-09-15
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast

class MixerActivity : Activity() {

    private lateinit var sideMenu: SideMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ ITINUGMA NA — btnMenuId → btnOpenMenuId + btnCloseMenuId!
            // ✅ TAMA NA ANG MGA ID — tugma sa totoong XML!
            sideMenu = SideMenu.setup(
                activity = this,
                drawerLayoutId = R.id.drawer_layout,
                btnOpenMenuId = R.id.btn_hamburger,    // ✅ BUKAS — tugma sa XML
                btnCloseMenuId = R.id.btn_close_menu   // ✅ ISARA — tugma sa XML
            )

            // ✅ GAIN KNOB — PERFECT NA!
            val gainKnob = findViewById<KnobView>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f
            gainKnob.value = 0f

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ✅ ITINUGMA NA — Gravity.START + public drawerLayout
    override fun onBackPressed() {
        if (::sideMenu.isInitialized && sideMenu.drawerLayout.isDrawerOpen(Gravity.START)) {
            sideMenu.close()
        } else {
            super.onBackPressed()
        }
    }
}
