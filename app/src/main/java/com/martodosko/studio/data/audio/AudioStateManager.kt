// ==================================================
// FILE: AudioStateManager.kt
// VERSION: v2.3.0-phase2-persistent-state-fix
// PURPOSE: Centralized Reactive State Holder with Basic Persistence
// NOTE: Fixed SharedPreferences clear() syntax.
// ==================================================
package com.martodosko.studio.data.audio

import android.content.Context
import androidx.core.content.edit
import com.martodosko.studio.domain.model.AudioParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioStateManager @Inject constructor(
    private val context: Context // ✅ INJECT CONTEXT FOR PREFS ACCESS
) {

    private val prefs = context.getSharedPreferences("MartodoskoAudioState", Context.MODE_PRIVATE)
    
    // Load initial state from disk if available, otherwise use defaults
    private val initialState = loadFromDisk() ?: AudioParams()
    
    private val _params = MutableStateFlow(initialState)
    
    /**
     * Exposes the current audio parameters as a read-only Flow.
     */
    val params: StateFlow<AudioParams> = _params.asStateFlow()

    /**
     * Updates a specific parameter atomically and saves to disk.
     */
    fun updateParam(updateBlock: (AudioParams) -> AudioParams) {
        val current = _params.value
        val updated = updateBlock(current).validate()
        _params.value = updated
        
        // ✅ SAVE TO DISK IMMEDIATELY
        saveToDisk(updated)
    }

    /**
     * Resets all parameters to default and clears disk storage.
     */
    fun reset() {
        val defaultParams = AudioParams().validate()
        _params.value = defaultParams
        
        // ✅ FIX: Use edit {} block to clear preferences properly
        prefs.edit {
            clear()
        }
        
        // Optionally save the default state back so it persists even after reset
        saveToDisk(defaultParams)
    }

    /**
     * Loads a preset into the state.
     */
    fun loadPreset(presetId: String, config: Map<String, Float>) {
        val newParams = _params.value.copy(
            vocalGain = config["vocalGain"] ?: _params.value.vocalGain,
            vocalVolume = config["vocalVolume"] ?: _params.value.vocalVolume,
            guitarDrive = config["guitarDrive"] ?: _params.value.guitarDrive,
            guitarLevel = config["guitarLevel"] ?: _params.value.guitarLevel,
            masterVolume = config["masterVolume"] ?: _params.value.masterVolume,
            activePresetId = presetId
        ).validate()
        
        _params.value = newParams
        saveToDisk(newParams)
    }

    // --- PRIVATE HELPERS FOR PERSISTENCE ---

    private fun saveToDisk(params: AudioParams) {
        prefs.edit {
            putFloat("vocal_gain", params.vocalGain)
            putFloat("vocal_volume", params.vocalVolume)
            putFloat("guitar_drive", params.guitarDrive)
            putFloat("guitar_level", params.guitarLevel)
            putFloat("master_volume", params.masterVolume)
            params.activePresetId?.let { putString("active_preset_id", it) }
        }
    }

    private fun loadFromDisk(): AudioParams? {
        return try {
            val vg = prefs.getFloat("vocal_gain", Float.NaN)
            val vv = prefs.getFloat("vocal_volume", Float.NaN)
            val gd = prefs.getFloat("guitar_drive", Float.NaN)
            val gl = prefs.getFloat("guitar_level", Float.NaN)
            val mv = prefs.getFloat("master_volume", Float.NaN)
            
            // If any critical value is NaN, assume no valid save exists yet
            if (vg.isNaN() || vv.isNaN()) return null
            
            AudioParams(
                vocalGain = vg,
                vocalVolume = vv,
                guitarDrive = gd,
                guitarLevel = gl,
                masterVolume = mv,
                activePresetId = prefs.getString("active_preset_id", null)
            )
        } catch (e: Exception) {
            null
        }
    }
}
