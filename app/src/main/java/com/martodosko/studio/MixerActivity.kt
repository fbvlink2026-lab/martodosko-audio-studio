// ==================================================
// FILE: MixerActivity.kt — ✅ GAIN KNOB LANG TALAGA! WALANG ERROR!
// VERSION: 2.4.1 — ✅ SENSITIVITY TINANGGAL MUNA! PARA WALANG UNRESOLVED REFERENCE!
// UPDATED: 2026-09-18 — ✅ TUGMA SA XML! WALANG HINDI NAKIKITANG ID!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    private lateinit var sideMenu: SideMenu

    // ==============================================
    // ✅ GAIN KNOB LANG MUNA — TUGMA SA XML!
    // ==============================================
    private lateinit var gainKnob: GainKnob

    // ⏸️ SENSITIVITY NAKA-COMMENT MUNA — KASI NAKA-COMMENT SA XML!
    // private lateinit var sensitivityKnob: SensitivityKnob

    // ==============================================
    // ⏸️ LAHAT NG IBA — NAKA-COMMENT MUNA!
    // ==============================================
    /* ... lahat ng iba ... */

    companion object {
        private const val PREFS_NAME = "MixerPrefs"
        // ✅ GAIN LANG — TUGMA SA XML!
        private const val KEY_GAIN_VALUE = "gain_value"

        // ⏸️ SENSITIVITY NAKA-COMMENT MUNA
        // private const val KEY_SENSITIVITY_VALUE = "sensitivity_value"

        /* ... lahat ng iba ... */
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ SIDE MENU — GUMAGANA
            sideMenu = SideMenu.setup(
                activity = this,
                drawerLayoutId = R.id.drawer_layout,
                btnOpenMenuId = R.id.btn_hamburger,
                btnCloseMenuId = R.id.btn_close_menu
            )

            // ==============================================
            // ✅ GAIN KNOB LANG — TUGMA SA XML!
            // ==============================================
            gainKnob = findViewById<GainKnob>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f

            // ⏸️ SENSITIVITY — NAKA-COMMENT MUNA
            // sensitivityKnob = findViewById<SensitivityKnob>(R.id.knob_sensitivity)
            // sensitivityKnob.labelText = "SENS"
            // sensitivityKnob.unitText = ""
            // sensitivityKnob.minValue = 0f
            // sensitivityKnob.maxValue = 100f

            loadSavedValues()

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ==============================================
    // ✅ SAVE — GAIN LANG!
    // ==============================================
    private fun saveValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putFloat(KEY_GAIN_VALUE, gainKnob.value)
        // editor.putFloat(KEY_SENSITIVITY_VALUE, sensitivityKnob.value)

        editor.apply()
        Toast.makeText(this, "✅ Naisave!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ LOAD — GAIN LANG!
    // ==============================================
    private fun loadSavedValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        gainKnob.value = prefs.getFloat(KEY_GAIN_VALUE, 0f)
        // sensitivityKnob.value = prefs.getFloat(KEY_SENSITIVITY_VALUE, 50f)
    }

    override fun onBackPressed() {
        saveValues()
        if (::sideMenu.isInitialized && sideMenu.drawerLayout.isDrawerOpen(Gravity.START)) {
            sideMenu.close()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        saveValues()
    }
}
