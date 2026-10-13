// ==================================================
// FILE: AudioParams.kt
// VERSION: v2.3.0-phase2-step1
// PURPOSE: Data Model for Real-Time Audio Parameters
// NOTE: Immutable data class to ensure thread safety in Flow emissions.
// ==================================================
package com.martodosko.studio.domain.model

/**
 * Represents the current state of all audio parameters.
 * This will be emitted by the State Manager and observed by both UI and Native Engine.
 */
data class AudioParams(
    val vocalGain: Float = 0f,      // dB (-inf to +inf, clamped in UI)
    val vocalVolume: Float = 80f,   // % (0-100)
    val guitarDrive: Float = 0f,    // dB (-inf to +inf)
    val guitarLevel: Float = 75f,   // % (0-100)
    val masterVolume: Float = 90f,  // % (0-100)
    val isMuted: Boolean = false,
    val activePresetId: String? = null
) {
    /**
     * Validates parameters before sending to native layer.
     * Ensures no NaN or Infinity values crash the DSP.
     */
    fun validate(): AudioParams {
        return copy(
            vocalGain = if (vocalGain.isNaN()) 0f else vocalGain.coerceIn(-60f, 20f),
            vocalVolume = if (vocalVolume.isNaN()) 0f else vocalVolume.coerceIn(0f, 100f),
            guitarDrive = if (guitarDrive.isNaN()) 0f else guitarDrive.coerceIn(-60f, 20f),
            guitarLevel = if (guitarLevel.isNaN()) 0f else guitarLevel.coerceIn(0f, 100f),
            masterVolume = if (masterVolume.isNaN()) 0f else masterVolume.coerceIn(0f, 100f)
        )
    }
}
