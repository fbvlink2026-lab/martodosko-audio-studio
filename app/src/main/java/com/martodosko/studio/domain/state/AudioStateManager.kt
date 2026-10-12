// ==================================================
// FILE: AudioStateManager.kt
// VERSION: 2.1.0
// PURPOSE: Centralized Reactive State for Audio Params
// NOTE: This replaces global variables and ensures 
//       Pedal <-> Mixer synchronization without coupling.
// ==================================================
package com.martodosko.studio.domain.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class representing the full state of a single channel (Vocal or Guitar).
 */
data class ChannelState(
    val gain: Float = 0f,          // -50 to +50 dB
    val sensitivity: Float = 50f,  // 0 to 100%
    val volume: Float = 80f,       // 0 to 100%
    val effectsMix: Float = 30f,   // 0 to 100% (Reverb/Delay blend)
    val noiseGate: Float = 20f     // Threshold level
)

/**
 * The Singleton Manager that holds the current audio configuration.
 * Injected via Hilt into ViewModels.
 */
@Singleton
class AudioStateManager @Inject constructor() {

    private val _vocalState = MutableStateFlow(ChannelState())
    val vocalState: StateFlow<ChannelState> = _vocalState.asStateFlow()

    private val _guitarState = MutableStateFlow(ChannelState())
    val guitarState: StateFlow<ChannelState> = _guitarState.asStateFlow()

    // --- UPDATE METHODS ---
    
    fun updateVocalGain(value: Float) {
        _vocalState.value = _vocalState.value.copy(gain = value.coerceIn(-50f, 50f))
        notifyEngine("vocal", "gain", value)
    }

    fun updateVocalVolume(value: Float) {
        _vocalState.value = _vocalState.value.copy(volume = value.coerceIn(0f, 100f))
        notifyEngine("vocal", "volume", value)
    }

    fun updateGuitarGain(value: Float) {
        _guitarState.value = _guitarState.value.copy(gain = value.coerceIn(-50f, 50f))
        notifyEngine("guitar", "gain", value)
    }

    fun updateGuitarVolume(value: Float) {
        _guitarState.value = _guitarState.value.copy(volume = value.coerceIn(0f, 100f))
        notifyEngine("guitar", "volume", value)
    }

    /**
     * Placeholder for JNI Call to C++ Engine.
     * In Phase 3, this will call native methods like:
     * NativeAudioBridge.setParam(channel, param, value)
     */
    private fun notifyEngine(channel: String, param: String, value: Float) {
        // TODO: Implement actual JNI bridge call here later
        println("🔊 ENGINE UPDATE: [$channel] $param = $value")
    }
}
