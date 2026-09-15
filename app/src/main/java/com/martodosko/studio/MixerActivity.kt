// ==================================================
// FILE: MixerActivity.kt — ✅ NAG-I-ISAVE NG HALAGA! HINDI NA MAWAWALA!
// VERSION: 2.1.0 — SharedPreferences! NAISAVE KAHIT LUMABAS O MAG-BACK!
// UPDATED: 2026-09-16
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast

class MixerActivity : Activity() {

    private lateinit var sideMenu: SideMenu
    private lateinit var gainKnob: KnobView

    // ✅ PANGALAN NG SAVED DATA
    companion object {
        private const val PREFS_NAME = "MixerPrefs"
        private const val KEY_GAIN_VALUE = "gain_value"
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

            // ✅ GAIN KNOB — KUKUNIN ANG HALAGA
            gainKnob = findViewById<KnobView>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f

            // ✅ BALIKAN ANG NAISAVE NA HALAGA — KUNG MERON!
            loadSavedValues()

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ==============================================
    // ✅ MAG-ISAVE BAGO MAGSARA! — ITO ANG SOLUSYON!
    // ==============================================
    private fun saveValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putFloat(KEY_GAIN_VALUE, gainKnob.value) // ✅ ISAVE ANG HALAGA NG GAIN
        editor.apply()
        Toast.makeText(this, "✅ Naisave: ${gainKnob.value.roundToInt()} dB", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ BALIKAN ANG NAISAVE NA HALAGA!
    // ==============================================
    private fun loadSavedValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedGain = prefs.getFloat(KEY_GAIN_VALUE, 0f) // ✅ 0f = default kung wala pang naka-save
        gainKnob.value = savedGain
    }

    // ✅ KAPAG PININDOT ANG BACK BUTTON — MAG-ISAVE MUNA!
    override fun onBackPressed() {
        saveValues() // ✅ ISAVE MUNA BAGO LUMABAS!
        if (::sideMenu.isInitialized && sideMenu.drawerLayout.isDrawerOpen(Gravity.START)) {
            sideMenu.close()
        } else {
            super.onBackPressed()
        }
    }

    // ✅ KAPAG PININDOT ANG MENU BUTTON (pumunta sa ibang screen) — MAG-ISAVE MUNA!
    override fun onPause() {
        super.onPause()
        if (::gainKnob.isInitialized) {
            saveValues() // ✅ ISAVE KAPAG UMALIS SA SCREEN — ANUMAN ANG DAHILAN!
        }
    }
}
