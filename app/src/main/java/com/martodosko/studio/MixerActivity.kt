// ==================================================
// FILE: MixerActivity.kt — ✅ LAHAT NG KNOBS MAY SAVE NA! WALANG TINANGGAL!
// VERSION: 2.2.0 — ✅ GAIN + EQ + REVERB + DELAY + CHORUS + DISTORTION + COMPRESSOR + OUTPUT!
// UPDATED: 2026-09-17 — ORIHINAL NA LOGIC NANDOON PA RIN! IDINAGDAG LANG ANG IBA!
// ==================================================
package com.martodosko.studio

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import kotlin.math.roundToInt  // ✅ ITO ANG KULANG! IDAGDAG LANG!

class MixerActivity : Activity() {

    private lateinit var sideMenu: SideMenu

    // ✅ LAHAT NG KNOB — DECLARE LANG!
    private lateinit var gainKnob: KnobView
    private lateinit var sensitivityKnob: KnobView
    private lateinit var eqLowKnob: KnobView
    private lateinit var eqLowMidKnob: KnobView
    private lateinit var eqMidKnob: KnobView
    private lateinit var eqHighMidKnob: KnobView
    private lateinit var eqHighKnob: KnobView
    private lateinit var eqPresenceKnob: KnobView
    private lateinit var reverbMixKnob: KnobView
    private lateinit var reverbDecayKnob: KnobView
    private lateinit var reverbPredelayKnob: KnobView
    private lateinit var delayTimeKnob: KnobView
    private lateinit var delayFeedbackKnob: KnobView
    private lateinit var delayMixKnob: KnobView
    private lateinit var chorusMixKnob: KnobView
    private lateinit var chorusSpeedKnob: KnobView
    private lateinit var chorusDepthKnob: KnobView
    private lateinit var distortionKnob: KnobView
    private lateinit var distortionToneKnob: KnobView
    private lateinit var compThresholdKnob: KnobView
    private lateinit var compRatioKnob: KnobView
    private lateinit var compAttackKnob: KnobView
    private lateinit var compReleaseKnob: KnobView
    private lateinit var compMakeupKnob: KnobView
    private lateinit var panKnob: KnobView
    private lateinit var channelVolKnob: KnobView

    // ✅ PANGALAN NG SAVED DATA — LAHAT NG KNOB MAY SARILING KEY!
    companion object {
        private const val PREFS_NAME = "MixerPrefs"
        // INPUT
        private const val KEY_GAIN_VALUE = "gain_value"
        private const val KEY_SENSITIVITY_VALUE = "sensitivity_value"
        // EQ
        private const val KEY_EQ_LOW = "eq_low"
        private const val KEY_EQ_LOWMID = "eq_lowmid"
        private const val KEY_EQ_MID = "eq_mid"
        private const val KEY_EQ_HIGHMID = "eq_highmid"
        private const val KEY_EQ_HIGH = "eq_high"
        private const val KEY_EQ_PRESENCE = "eq_presence"
        // REVERB
        private const val KEY_REVERB_MIX = "reverb_mix"
        private const val KEY_REVERB_DECAY = "reverb_decay"
        private const val KEY_REVERB_PREDELAY = "reverb_predelay"
        // DELAY
        private const val KEY_DELAY_TIME = "delay_time"
        private const val KEY_DELAY_FEEDBACK = "delay_feedback"
        private const val KEY_DELAY_MIX = "delay_mix"
        // CHORUS
        private const val KEY_CHORUS_MIX = "chorus_mix"
        private const val KEY_CHORUS_SPEED = "chorus_speed"
        private const val KEY_CHORUS_DEPTH = "chorus_depth"
        // DISTORTION
        private const val KEY_DISTORTION = "distortion"
        private const val KEY_DIST_TONE = "dist_tone"
        // COMPRESSOR
        private const val KEY_COMP_THRESHOLD = "comp_threshold"
        private const val KEY_COMP_RATIO = "comp_ratio"
        private const val KEY_COMP_ATTACK = "comp_attack"
        private const val KEY_COMP_RELEASE = "comp_release"
        private const val KEY_COMP_MAKEUP = "comp_makeup"
        // OUTPUT
        private const val KEY_PAN = "pan"
        private const val KEY_CHANNEL_VOL = "channel_vol"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mixer)

        try {
            // ✅ SIDE MENU — GUMAGANA PA RIN! WALANG PINAGBAGO!
            sideMenu = SideMenu.setup(
                activity = this,
                drawerLayoutId = R.id.drawer_layout,
                btnOpenMenuId = R.id.btn_hamburger,
                btnCloseMenuId = R.id.btn_close_menu
            )

            // ==============================================
            // ✅ INPUT SECTION — GAIN + SENSITIVITY
            // ==============================================
            gainKnob = findViewById<KnobView>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f

            sensitivityKnob = findViewById<KnobView>(R.id.knob_sensitivity)
            sensitivityKnob.labelText = "SENS"
            sensitivityKnob.unitText = ""
            sensitivityKnob.minValue = 0f
            sensitivityKnob.maxValue = 100f

            // ==============================================
            // ✅ EQ SECTION — LOW, LOW-MID, MID, HIGH-MID, HIGH, PRESENCE
            // ==============================================
            eqLowKnob = findViewById<KnobView>(R.id.knob_eq_low)
            eqLowKnob.labelText = "LOW"
            eqLowKnob.unitText = "dB"
            eqLowKnob.minValue = -12f
            eqLowKnob.maxValue = 12f

            eqLowMidKnob = findViewById<KnobView>(R.id.knob_eq_lowmid)
            eqLowMidKnob.labelText = "LOW-MID"
            eqLowMidKnob.unitText = "dB"
            eqLowMidKnob.minValue = -12f
            eqLowMidKnob.maxValue = 12f

            eqMidKnob = findViewById<KnobView>(R.id.knob_eq_mid)
            eqMidKnob.labelText = "MID"
            eqMidKnob.unitText = "dB"
            eqMidKnob.minValue = -12f
            eqMidKnob.maxValue = 12f

            eqHighMidKnob = findViewById<KnobView>(R.id.knob_eq_highmid)
            eqHighMidKnob.labelText = "HIGH-MID"
            eqHighMidKnob.unitText = "dB"
            eqHighMidKnob.minValue = -12f
            eqHighMidKnob.maxValue = 12f

            eqHighKnob = findViewById<KnobView>(R.id.knob_eq_high)
            eqHighKnob.labelText = "HIGH"
            eqHighKnob.unitText = "dB"
            eqHighKnob.minValue = -12f
            eqHighKnob.maxValue = 12f

            eqPresenceKnob = findViewById<KnobView>(R.id.knob_eq_presence)
            eqPresenceKnob.labelText = "PRESENCE"
            eqPresenceKnob.unitText = "dB"
            eqPresenceKnob.minValue = -12f
            eqPresenceKnob.maxValue = 12f

            // ==============================================
            // ✅ REVERB SECTION — MIX, DECAY, PRE-DELAY
            // ==============================================
            reverbMixKnob = findViewById<KnobView>(R.id.knob_reverb_mix)
            reverbMixKnob.labelText = "MIX"
            reverbMixKnob.unitText = "%"
            reverbMixKnob.minValue = 0f
            reverbMixKnob.maxValue = 100f

            reverbDecayKnob = findViewById<KnobView>(R.id.knob_reverb_decay)
            reverbDecayKnob.labelText = "DECAY"
            reverbDecayKnob.unitText = "s"
            reverbDecayKnob.minValue = 0.1f
            reverbDecayKnob.maxValue = 5f

            reverbPredelayKnob = findViewById<KnobView>(R.id.knob_reverb_predelay)
            reverbPredelayKnob.labelText = "PRE-DELAY"
            reverbPredelayKnob.unitText = "ms"
            reverbPredelayKnob.minValue = 0f
            reverbPredelayKnob.maxValue = 100f

            // ==============================================
            // ✅ DELAY SECTION — TIME, FEEDBACK, MIX
            // ==============================================
            delayTimeKnob = findViewById<KnobView>(R.id.knob_delay_time)
            delayTimeKnob.labelText = "TIME"
            delayTimeKnob.unitText = "ms"
            delayTimeKnob.minValue = 10f
            delayTimeKnob.maxValue = 1000f

            delayFeedbackKnob = findViewById<KnobView>(R.id.knob_delay_feedback)
            delayFeedbackKnob.labelText = "FEEDBACK"
            delayFeedbackKnob.unitText = "%"
            delayFeedbackKnob.minValue = 0f
            delayFeedbackKnob.maxValue = 100f

            delayMixKnob = findViewById<KnobView>(R.id.knob_delay_mix)
            delayMixKnob.labelText = "MIX"
            delayMixKnob.unitText = "%"
            delayMixKnob.minValue = 0f
            delayMixKnob.maxValue = 100f

            // ==============================================
            // ✅ CHORUS SECTION — MIX, SPEED, DEPTH
            // ==============================================
            chorusMixKnob = findViewById<KnobView>(R.id.knob_chorus_mix)
            chorusMixKnob.labelText = "MIX"
            chorusMixKnob.unitText = "%"
            chorusMixKnob.minValue = 0f
            chorusMixKnob.maxValue = 100f

            chorusSpeedKnob = findViewById<KnobView>(R.id.knob_chorus_speed)
            chorusSpeedKnob.labelText = "SPEED"
            chorusSpeedKnob.unitText = "Hz"
            chorusSpeedKnob.minValue = 0.1f
            chorusSpeedKnob.maxValue = 10f

            chorusDepthKnob = findViewById<KnobView>(R.id.knob_chorus_depth)
            chorusDepthKnob.labelText = "DEPTH"
            chorusDepthKnob.unitText = ""
            chorusDepthKnob.minValue = 0f
            chorusDepthKnob.maxValue = 100f

            // ==============================================
            // ✅ DISTORTION SECTION — DISTORTION, TONE
            // ==============================================
            distortionKnob = findViewById<KnobView>(R.id.knob_distortion)
            distortionKnob.labelText = "GAIN"
            distortionKnob.unitText = ""
            distortionKnob.minValue = 0f
            distortionKnob.maxValue = 100f

            distortionToneKnob = findViewById<KnobView>(R.id.knob_dist_tone)
            distortionToneKnob.labelText = "TONE"
            distortionToneKnob.unitText = ""
            distortionToneKnob.minValue = 0f
            distortionToneKnob.maxValue = 100f

            // ==============================================
            // ✅ COMPRESSOR SECTION — THRESHOLD, RATIO, ATTACK, RELEASE, MAKEUP
            // ==============================================
            compThresholdKnob = findViewById<KnobView>(R.id.knob_comp_threshold)
            compThresholdKnob.labelText = "THRESHOLD"
            compThresholdKnob.unitText = "dB"
            compThresholdKnob.minValue = -60f
            compThresholdKnob.maxValue = 0f

            compRatioKnob = findViewById<KnobView>(R.id.knob_comp_ratio)
            compRatioKnob.labelText = "RATIO"
            compRatioKnob.unitText = ":1"
            compRatioKnob.minValue = 1f
            compRatioKnob.maxValue = 20f

            compAttackKnob = findViewById<KnobView>(R.id.knob_comp_attack)
            compAttackKnob.labelText = "ATTACK"
            compAttackKnob.unitText = "ms"
            compAttackKnob.minValue = 0.1f
            compAttackKnob.maxValue = 100f

            compReleaseKnob = findViewById<KnobView>(R.id.knob_comp_release)
            compReleaseKnob.labelText = "RELEASE"
            compReleaseKnob.unitText = "ms"
            compReleaseKnob.minValue = 10f
            compReleaseKnob.maxValue = 500f

            compMakeupKnob = findViewById<KnobView>(R.id.knob_comp_makeup)
            compMakeupKnob.labelText = "MAKEUP"
            compMakeupKnob.unitText = "dB"
            compMakeupKnob.minValue = 0f
            compMakeupKnob.maxValue = 24f

            // ==============================================
            // ✅ OUTPUT SECTION — PAN, CHANNEL VOLUME
            // ==============================================
            panKnob = findViewById<KnobView>(R.id.knob_pan)
            panKnob.labelText = "PAN"
            panKnob.unitText = ""
            panKnob.minValue = -100f
            panKnob.maxValue = 100f

            channelVolKnob = findViewById<KnobView>(R.id.knob_channel_volume)
            channelVolKnob.labelText = "VOLUME"
            channelVolKnob.unitText = "dB"
            channelVolKnob.minValue = -50f
            channelVolKnob.maxValue = 10f

            // ✅ BALIKAN ANG NAISAVE NA HALAGA — KUNG MERON!
            loadSavedValues()

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ==============================================
    // ✅ MAG-ISAVE BAGO MAGSARA! — LAHAT NG KNOB!
    // ==============================================
    private fun saveValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // INPUT
        editor.putFloat(KEY_GAIN_VALUE, gainKnob.value)
        editor.putFloat(KEY_SENSITIVITY_VALUE, sensitivityKnob.value)
        // EQ
        editor.putFloat(KEY_EQ_LOW, eqLowKnob.value)
        editor.putFloat(KEY_EQ_LOWMID, eqLowMidKnob.value)
        editor.putFloat(KEY_EQ_MID, eqMidKnob.value)
        editor.putFloat(KEY_EQ_HIGHMID, eqHighMidKnob.value)
        editor.putFloat(KEY_EQ_HIGH, eqHighKnob.value)
        editor.putFloat(KEY_EQ_PRESENCE, eqPresenceKnob.value)
        // REVERB
        editor.putFloat(KEY_REVERB_MIX, reverbMixKnob.value)
        editor.putFloat(KEY_REVERB_DECAY, reverbDecayKnob.value)
        editor.putFloat(KEY_REVERB_PREDELAY, reverbPredelayKnob.value)
        // DELAY
        editor.putFloat(KEY_DELAY_TIME, delayTimeKnob.value)
        editor.putFloat(KEY_DELAY_FEEDBACK, delayFeedbackKnob.value)
        editor.putFloat(KEY_DELAY_MIX, delayMixKnob.value)
        // CHORUS
        editor.putFloat(KEY_CHORUS_MIX, chorusMixKnob.value)
        editor.putFloat(KEY_CHORUS_SPEED, chorusSpeedKnob.value)
        editor.putFloat(KEY_CHORUS_DEPTH, chorusDepthKnob.value)
        // DISTORTION
        editor.putFloat(KEY_DISTORTION, distortionKnob.value)
        editor.putFloat(KEY_DIST_TONE, distortionToneKnob.value)
        // COMPRESSOR
        editor.putFloat(KEY_COMP_THRESHOLD, compThresholdKnob.value)
        editor.putFloat(KEY_COMP_RATIO, compRatioKnob.value)
        editor.putFloat(KEY_COMP_ATTACK, compAttackKnob.value)
        editor.putFloat(KEY_COMP_RELEASE, compReleaseKnob.value)
        editor.putFloat(KEY_COMP_MAKEUP, compMakeupKnob.value)
        // OUTPUT
        editor.putFloat(KEY_PAN, panKnob.value)
        editor.putFloat(KEY_CHANNEL_VOL, channelVolKnob.value)

        editor.apply()
        Toast.makeText(this, "✅ Naisave ang lahat ng settings!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ BALIKAN ANG NAISAVE NA HALAGA! — LAHAT NG KNOB!
    // ==============================================
    private fun loadSavedValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // INPUT
        gainKnob.value = prefs.getFloat(KEY_GAIN_VALUE, 0f)
        sensitivityKnob.value = prefs.getFloat(KEY_SENSITIVITY_VALUE, 50f)
        // EQ
        eqLowKnob.value = prefs.getFloat(KEY_EQ_LOW, 0f)
        eqLowMidKnob.value = prefs.getFloat(KEY_EQ_LOWMID, 0f)
        eqMidKnob.value = prefs.getFloat(KEY_EQ_MID, 0f)
        eqHighMidKnob.value = prefs.getFloat(KEY_EQ_HIGHMID, 0f)
        eqHighKnob.value = prefs.getFloat(KEY_EQ_HIGH, 0f)
        eqPresenceKnob.value = prefs.getFloat(KEY_EQ_PRESENCE, 0f)
        // REVERB
        reverbMixKnob.value = prefs.getFloat(KEY_REVERB_MIX, 30f)
        reverbDecayKnob.value = prefs.getFloat(KEY_REVERB_DECAY, 1.5f)
        reverbPredelayKnob.value = prefs.getFloat(KEY_REVERB_PREDELAY, 20f)
        // DELAY
        delayTimeKnob.value = prefs.getFloat(KEY_DELAY_TIME, 250f)
        delayFeedbackKnob.value = prefs.getFloat(KEY_DELAY_FEEDBACK, 40f)
        delayMixKnob.value = prefs.getFloat(KEY_DELAY_MIX, 20f)
        // CHORUS
        chorusMixKnob.value = prefs.getFloat(KEY_CHORUS_MIX, 0f)
        chorusSpeedKnob.value = prefs.getFloat(KEY_CHORUS_SPEED, 1.5f)
        chorusDepthKnob.value = prefs.getFloat(KEY_CHORUS_DEPTH, 30f)
        // DISTORTION
        distortionKnob.value = prefs.getFloat(KEY_DISTORTION, 0f)
        distortionToneKnob.value = prefs.getFloat(KEY_DIST_TONE, 50f)
        // COMPRESSOR
        compThresholdKnob.value = prefs.getFloat(KEY_COMP_THRESHOLD, -24f)
        compRatioKnob.value = prefs.getFloat(KEY_COMP_RATIO, 4f)
        compAttackKnob.value = prefs.getFloat(KEY_COMP_ATTACK, 10f)
        compReleaseKnob.value = prefs.getFloat(KEY_COMP_RELEASE, 100f)
        compMakeupKnob.value = prefs.getFloat(KEY_COMP_MAKEUP, 0f)
        // OUTPUT
        panKnob.value = prefs.getFloat(KEY_PAN, 0f)
        channelVolKnob.value = prefs.getFloat(KEY_CHANNEL_VOL, 0f)
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
        saveValues() // ✅ ISAVE KAPAG UMALIS SA SCREEN — ANUMAN ANG DAHILAN!
    }
}
