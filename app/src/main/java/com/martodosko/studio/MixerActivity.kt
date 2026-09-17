// ==================================================
// FILE: MixerActivity.kt — ✅ GAIN + SENSITIVITY! NAISAVE KAHIT LUMABAS!
// VERSION: 2.2.0 — ✅ DAGDAG: SENSITIVITY! SHARED PREFERENCES PA RIN!
// UPDATED: 2026-09-18 — WALANG BINAGO SA IBA — SENSITIVITY LANG ANG IDINAGDAG!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import kotlin.math.roundToInt

class MixerActivity : Activity() {

    private lateinit var sideMenu: SideMenu
    private lateinit var gainKnob: KnobView
    private lateinit var sensitivityKnob: KnobView // ✅ DAGDAG

    // ✅ PANGALAN NG SAVED DATA — DAGDAG ANG SENSITIVITY
    companion object {
        private const val PREFS_NAME = "MixerPrefs"
        private const val KEY_GAIN_VALUE = "gain_value"
        private const val KEY_SENSITIVITY_VALUE = "sensitivity_value" // ✅ DAGDAG
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ SIDE MENU — GUMAGANA PA RIN!
            sideMenu = SideMenu.setup(
                activity = this,
                drawerLayoutId = R.id.drawer_layout,
                btnOpenMenuId = R.id.btn_hamburger,
                btnCloseMenuId = R.id.btn_close_menu
            )

            // ✅ GAIN KNOB — GAYA NG DATI
            gainKnob = findViewById<KnobView>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f

            // ✅ SENSITIVITY KNOB — BAGONG DAGDAG!
            sensitivityKnob = findViewById<KnobView>(R.id.knob_sensitivity)
            sensitivityKnob.labelText = "SENS"
            sensitivityKnob.unitText = ""
            sensitivityKnob.minValue = 0f
            sensitivityKnob.maxValue = 100f

            // ✅ BALIKAN ANG NAISAVE NA HALAGA — KUNG MERON!
            loadSavedValues()

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ==============================================
    // ✅ MAG-ISAVE BAGO MAGSARA! — DAGDAG ANG SENSITIVITY!
    // ==============================================
    private fun saveValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putFloat(KEY_GAIN_VALUE, gainKnob.value)
        editor.putFloat(KEY_SENSITIVITY_VALUE, sensitivityKnob.value) // ✅ DAGDAG
        editor.apply()
        Toast.makeText(this, "✅ Naisave!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ BALIKAN ANG NAISAVE NA HALAGA! — DAGDAG ANG SENSITIVITY!
    // ==============================================
    private fun loadSavedValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        gainKnob.value = prefs.getFloat(KEY_GAIN_VALUE, 0f)
        sensitivityKnob.value = prefs.getFloat(KEY_SENSITIVITY_VALUE, 50f) // ✅ DAGDAG — default 50
    }

    // ✅ KAPAG PININDOT ANG BACK BUTTON — MAG-ISAVE MUNA!
    override fun onBackPressed() {
        saveValues()
        if (::sideMenu.isInitialized && sideMenu.drawerLayout.isDrawerOpen(Gravity.START)) {
            sideMenu.close()
        } else {
            super.onBackPressed()
        }
    }

    // ✅ KAPAG PININDOT ANG MENU BUTTON — MAG-ISAVE MUNA!
    override fun onPause() {
        super.onPause()
        if (::gainKnob.isInitialized && ::sensitivityKnob.isInitialized) {
            saveValues()
        }
    }
}
