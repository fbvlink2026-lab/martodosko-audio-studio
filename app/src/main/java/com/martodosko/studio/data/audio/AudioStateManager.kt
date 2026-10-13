// ==================================================
// FILE: AudioStateManager.kt
// VERSION: v2.3.0-phase2-step1
// PURPOSE: Centralized Reactive State Holder for Audio Params
// NOTE: Uses MutableStateFlow for immediate propagation to UI and Native Bridge.
// ==================================================
package com.martodosko.studio.data.audio

import com.martodosko.studio.domain.model.AudioParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioStateManager @Inject constructor() {

    private val _params = MutableStateFlow(AudioParams())
    
    /**
     * Exposes the current audio parameters as a read-only Flow.
     * UI Components observe this to update knobs/sliders.
     * Native Bridge observes this to push updates to C++.
     */
    val params: StateFlow<AudioParams> = _params.asStateFlow()

    /**
     * Updates a specific parameter atomically.
     * Called by UI interactions (e.g., Knob Drag).
     */
    fun updateParam(updateBlock: (AudioParams) -> AudioParams) {
        val current = _params.value
        val updated = updateBlock(current).validate()
        _params.value = updated
        
        // TODO: Phase 2 Step 2 - Notify Native Bridge here via JNI call
        // notifyNativeEngine(updated)
    }

    /**
     * Resets all parameters to default.
     */
    fun reset() {
        _params.value = AudioParams().validate()
    }

    /**
     * Loads a preset into the state.
     * Will be connected to Room/Supabase in Phase 3.
     */
    fun loadPreset(presetId: String, config: Map<String, Float>) {
        val newParams = _params.value.copy(
            vocalGain = config["vocalGain"] ?: _params.value.vocalGain,
            vocalVolume = config["vocalVolume"] ?: _params.value.vocalVolume,
            guitarDrive = config["guitarDrive"] ?: _params.value.guitarDrive,
            guitarLevel = config["guitarLevel"] ?: _params.value.guitarLevel,
            activePresetId = presetId
        ).validate()
        
        _params.value = newParams
    }
}
