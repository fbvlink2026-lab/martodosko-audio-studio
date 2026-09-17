// ==================================================
// FILE: MixerActivity.kt — ✅ PAISA-ISA LANG! GAIN + SENSITIVITY MUNA!
// VERSION: 2.4.0 — ✅ LAHAT IBA NAKA-COMMENT MUNA! PARA MALAMAN ANG SANHI NG CRASH!
// UPDATED: 2026-09-18 — WALANG TINANGGAL, WALANG BINAGO — NAKA-COMMENT LANG ANG IBA!
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
    // ✅ NAKABUKAS MUNA — GAIN + SENSITIVITY LANG!
    // ==============================================
    private lateinit var gainKnob: GainKnob
    private lateinit var sensitivityKnob: SensitivityKnob

    // ==============================================
    // ⏸️ NAKA-COMMENT MUNA — PARA MALAMAN ANG SANHI!
    // ==============================================
    /*
    private lateinit var eqLowKnob: EqLowKnob
    private lateinit var eqLowMidKnob: EqLowMidKnob
    private lateinit var eqMidKnob: EqMidKnob
    private lateinit var eqHighMidKnob: EqHighMidKnob
    private lateinit var eqHighKnob: EqHighKnob
    private lateinit var eqPresenceKnob: EqPresenceKnob
    private lateinit var reverbMixKnob: ReverbMixKnob
    private lateinit var reverbDecayKnob: ReverbDecayKnob
    private lateinit var reverbPredelayKnob: ReverbPredelayKnob
    private lateinit var delayTimeKnob: DelayTimeKnob
    private lateinit var delayFeedbackKnob: DelayFeedbackKnob
    private lateinit var delayMixKnob: DelayMixKnob
    private lateinit var chorusMixKnob: ChorusMixKnob
    private lateinit var chorusSpeedKnob: ChorusSpeedKnob
    private lateinit var chorusDepthKnob: ChorusDepthKnob
    private lateinit var distortionKnob: DistortionKnob
    private lateinit var distortionToneKnob: DistortionToneKnob
    private lateinit var compThresholdKnob: CompThresholdKnob
    private lateinit var compRatioKnob: CompRatioKnob
    private lateinit var compAttackKnob: CompAttackKnob
    private lateinit var compReleaseKnob: CompReleaseKnob
    private lateinit var compMakeupKnob: CompMakeupKnob
    private lateinit var panKnob: PanKnob
    private lateinit var channelVolKnob: ChannelVolumeKnob
    private lateinit var masterSlider: MasterVolumeSlider
    private lateinit var leftMonitorSlider: LeftMonitorSlider
    private lateinit var rightMonitorSlider: RightMonitorSlider
    */

    companion object {
        private const val PREFS_NAME = "MixerPrefs"
        // ✅ ACTIVE — GAIN + SENSITIVITY
        private const val KEY_GAIN_VALUE = "gain_value"
        private const val KEY_SENSITIVITY_VALUE = "sensitivity_value"

        // ⏸️ NAKA-COMMENT MUNA
        /*
        private const val KEY_EQ_LOW = "eq_low"
        private const val KEY_EQ_LOWMID = "eq_lowmid"
        private const val KEY_EQ_MID = "eq_mid"
        private const val KEY_EQ_HIGHMID = "eq_highmid"
        private const val KEY_EQ_HIGH = "eq_high"
        private const val KEY_EQ_PRESENCE = "eq_presence"
        private const val KEY_REVERB_MIX = "reverb_mix"
        private const val KEY_REVERB_DECAY = "reverb_decay"
        private const val KEY_REVERB_PREDELAY = "reverb_predelay"
        private const val KEY_DELAY_TIME = "delay_time"
        private const val KEY_DELAY_FEEDBACK = "delay_feedback"
        private const val KEY_DELAY_MIX = "delay_mix"
        private const val KEY_CHORUS_MIX = "chorus_mix"
        private const val KEY_CHORUS_SPEED = "chorus_speed"
        private const val KEY_CHORUS_DEPTH = "chorus_depth"
        private const val KEY_DISTORTION = "distortion"
        private const val KEY_DIST_TONE = "dist_tone"
        private const val KEY_COMP_THRESHOLD = "comp_threshold"
        private const val KEY_COMP_RATIO = "comp_ratio"
        private const val KEY_COMP_ATTACK = "comp_attack"
        private const val KEY_COMP_RELEASE = "comp_release"
        private const val KEY_COMP_MAKEUP = "comp_makeup"
        private const val KEY_PAN = "pan"
        private const val KEY_CHANNEL_VOL = "channel_vol"
        private const val KEY_MASTER_VOL = "master_vol"
        private const val KEY_LEFT_MONITOR = "left_monitor"
        private const val KEY_RIGHT_MONITOR = "right_monitor"
        */
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
            // ✅ NAKABUKAS MUNA — GAIN + SENSITIVITY LANG!
            // ==============================================
            gainKnob = findViewById<GainKnob>(R.id.knob_gain)
            gainKnob.labelText = "GAIN"
            gainKnob.unitText = "dB"
            gainKnob.minValue = -50f
            gainKnob.maxValue = 50f

            sensitivityKnob = findViewById<SensitivityKnob>(R.id.knob_sensitivity)
            sensitivityKnob.labelText = "SENS"
            sensitivityKnob.unitText = ""
            sensitivityKnob.minValue = 0f
            sensitivityKnob.maxValue = 100f

            // ==============================================
            // ⏸️ NAKA-COMMENT MUNA — HINDI PA TINATAWAG!
            // ==============================================
            /*
            // EQ SECTION
            eqLowKnob = findViewById<EqLowKnob>(R.id.knob_eq_low)
            eqLowKnob.labelText = "LOW"
            eqLowKnob.unitText = "dB"
            eqLowKnob.minValue = -12f
            eqLowKnob.maxValue = 12f

            eqLowMidKnob = findViewById<EqLowMidKnob>(R.id.knob_eq_lowmid)
            eqLowMidKnob.labelText = "LOW-MID"
            eqLowMidKnob.unitText = "dB"
            eqLowMidKnob.minValue = -12f
            eqLowMidKnob.maxValue = 12f

            eqMidKnob = findViewById<EqMidKnob>(R.id.knob_eq_mid)
            eqMidKnob.labelText = "MID"
            eqMidKnob.unitText = "dB"
            eqMidKnob.minValue = -12f
            eqMidKnob.maxValue = 12f

            eqHighMidKnob = findViewById<EqHighMidKnob>(R.id.knob_eq_highmid)
            eqHighMidKnob.labelText = "HIGH-MID"
            eqHighMidKnob.unitText = "dB"
            eqHighMidKnob.minValue = -12f
            eqHighMidKnob.maxValue = 12f

            eqHighKnob = findViewById<EqHighKnob>(R.id.knob_eq_high)
            eqHighKnob.labelText = "HIGH"
            eqHighKnob.unitText = "dB"
            eqHighKnob.minValue = -12f
            eqHighKnob.maxValue = 12f

            eqPresenceKnob = findViewById<EqPresenceKnob>(R.id.knob_eq_presence)
            eqPresenceKnob.labelText = "PRESENCE"
            eqPresenceKnob.unitText = "dB"
            eqPresenceKnob.minValue = -12f
            eqPresenceKnob.maxValue = 12f

            // REVERB SECTION
            reverbMixKnob = findViewById<ReverbMixKnob>(R.id.knob_reverb_mix)
            reverbMixKnob.labelText = "MIX"
            reverbMixKnob.unitText = "%"
            reverbMixKnob.minValue = 0f
            reverbMixKnob.maxValue = 100f

            reverbDecayKnob = findViewById<ReverbDecayKnob>(R.id.knob_reverb_decay)
            reverbDecayKnob.labelText = "DECAY"
            reverbDecayKnob.unitText = "s"
            reverbDecayKnob.minValue = 0.1f
            reverbDecayKnob.maxValue = 5f

            reverbPredelayKnob = findViewById<ReverbPredelayKnob>(R.id.knob_reverb_predelay)
            reverbPredelayKnob.labelText = "PRE-DELAY"
            reverbPredelayKnob.unitText = "ms"
            reverbPredelayKnob.minValue = 0f
            reverbPredelayKnob.maxValue = 100f

            // DELAY SECTION
            delayTimeKnob = findViewById<DelayTimeKnob>(R.id.knob_delay_time)
            delayTimeKnob.labelText = "TIME"
            delayTimeKnob.unitText = "ms"
            delayTimeKnob.minValue = 10f
            delayTimeKnob.maxValue = 1000f

            delayFeedbackKnob = findViewById<DelayFeedbackKnob>(R.id.knob_delay_feedback)
            delayFeedbackKnob.labelText = "FEEDBACK"
            delayFeedbackKnob.unitText = "%"
            delayFeedbackKnob.minValue = 0f
            delayFeedbackKnob.maxValue = 100f

            delayMixKnob = findViewById<DelayMixKnob>(R.id.knob_delay_mix)
            delayMixKnob.labelText = "MIX"
            delayMixKnob.unitText = "%"
            delayMixKnob.minValue = 0f
            delayMixKnob.maxValue = 100f

            // CHORUS SECTION
            chorusMixKnob = findViewById<ChorusMixKnob>(R.id.knob_chorus_mix)
            chorusMixKnob.labelText = "MIX"
            chorusMixKnob.unitText = "%"
            chorusMixKnob.minValue = 0f
            chorusMixKnob.maxValue = 100f

            chorusSpeedKnob = findViewById<ChorusSpeedKnob>(R.id.knob_chorus_speed)
            chorusSpeedKnob.labelText = "SPEED"
            chorusSpeedKnob.unitText = "Hz"
            chorusSpeedKnob.minValue = 0.1f
            chorusSpeedKnob.maxValue = 10f

            chorusDepthKnob = findViewById<ChorusDepthKnob>(R.id.knob_chorus_depth)
            chorusDepthKnob.labelText = "DEPTH"
            chorusDepthKnob.unitText = ""
            chorusDepthKnob.minValue = 0f
            chorusDepthKnob.maxValue = 100f

            // DISTORTION SECTION
            distortionKnob = findViewById<DistortionKnob>(R.id.knob_distortion)
            distortionKnob.labelText = "GAIN"
            distortionKnob.unitText = ""
            distortionKnob.minValue = 0f
            distortionKnob.maxValue = 100f

            distortionToneKnob = findViewById<DistortionToneKnob>(R.id.knob_dist_tone)
            distortionToneKnob.labelText = "TONE"
            distortionToneKnob.unitText = ""
            distortionToneKnob.minValue = 0f
            distortionToneKnob.maxValue = 100f

            // COMPRESSOR SECTION
            compThresholdKnob = findViewById<CompThresholdKnob>(R.id.knob_comp_threshold)
            compThresholdKnob.labelText = "THRESHOLD"
            compThresholdKnob.unitText = "dB"
            compThresholdKnob.minValue = -60f
            compThresholdKnob.maxValue = 0f

            compRatioKnob = findViewById<CompRatioKnob>(R.id.knob_comp_ratio)
            compRatioKnob.labelText = "RATIO"
            compRatioKnob.unitText = ":1"
            compRatioKnob.minValue = 1f
            compRatioKnob.maxValue = 20f

            compAttackKnob = findViewById<CompAttackKnob>(R.id.knob_comp_attack)
            compAttackKnob.labelText = "ATTACK"
            compAttackKnob.unitText = "ms"
            compAttackKnob.minValue = 0.1f
            compAttackKnob.maxValue = 100f

            compReleaseKnob = findViewById<CompReleaseKnob>(R.id.knob_comp_release)
            compReleaseKnob.labelText = "RELEASE"
            compReleaseKnob.unitText = "ms"
            compReleaseKnob.minValue = 10f
            compReleaseKnob.maxValue = 500f

            compMakeupKnob = findViewById<CompMakeupKnob>(R.id.knob_comp_makeup)
            compMakeupKnob.labelText = "MAKEUP"
            compMakeupKnob.unitText = "dB"
            compMakeupKnob.minValue = 0f
            compMakeupKnob.maxValue = 24f

            // OUTPUT SECTION
            panKnob = findViewById<PanKnob>(R.id.knob_pan)
            panKnob.labelText = "PAN"
            panKnob.unitText = ""
            panKnob.minValue = -100f
            panKnob.maxValue = 100f

            channelVolKnob = findViewById<ChannelVolumeKnob>(R.id.knob_channel_volume)
            channelVolKnob.labelText = "VOLUME"
            channelVolKnob.unitText = "dB"
            channelVolKnob.minValue = -50f
            channelVolKnob.maxValue = 10f

            // MASTER SLIDERS
            masterSlider = findViewById<MasterVolumeSlider>(R.id.slider_master)
            leftMonitorSlider = findViewById<LeftMonitorSlider>(R.id.slider_master_left)
            rightMonitorSlider = findViewById<RightMonitorSlider>(R.id.slider_master_right)
            */

            // ✅ LOAD — GAIN + SENSITIVITY LANG MUNA!
            loadSavedValues()

        } catch (e: Exception) {
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // ==============================================
    // ✅ SAVE — GAIN + SENSITIVITY LANG MUNA!
    // ==============================================
    private fun saveValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putFloat(KEY_GAIN_VALUE, gainKnob.value)
        editor.putFloat(KEY_SENSITIVITY_VALUE, sensitivityKnob.value)

        editor.apply()
        Toast.makeText(this, "✅ Naisave!", Toast.LENGTH_SHORT).show()
    }

    // ==============================================
    // ✅ LOAD — GAIN + SENSITIVITY LANG MUNA!
    // ==============================================
    private fun loadSavedValues() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        gainKnob.value = prefs.getFloat(KEY_GAIN_VALUE, 0f)
        sensitivityKnob.value = prefs.getFloat(KEY_SENSITIVITY_VALUE, 50f)
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
