// ==================================================
// FILE: MixerViewModel.kt
// VERSION: v2.3.0-phase2-gain-sync-fix
// PURPOSE: Bridges Mixer UI with AudioStateManager for ALL parameters
// NOTE: Added handlers for Vocal Gain and Guitar Drive to enable persistence.
// ==================================================
package com.martodosko.studio.ui.screens.mixer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodosko.studio.data.audio.AudioStateManager
import com.martodosko.studio.domain.model.AudioParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MixerViewModel @Inject constructor(
    private val audioManager: AudioStateManager
) : ViewModel() {

    val audioParams: StateFlow<AudioParams> = audioManager.params
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AudioParams()
        )

    // ✅ EXISTING VOLUME HANDLERS
    fun onVocalVolumeChange(value: Float) {
        audioManager.updateParam { it.copy(vocalVolume = value) }
    }

    fun onGuitarLevelChange(value: Float) {
        audioManager.updateParam { it.copy(guitarLevel = value) }
    }
    
    fun onMasterVolumeChange(value: Float) {
        audioManager.updateParam { it.copy(masterVolume = value) }
    }

    // ✅ NEW GAIN/DRIVE HANDLERS TO ENABLE PERSISTENCE
    fun onVocalGainChange(value: Float) {
        audioManager.updateParam { it.copy(vocalGain = value) }
    }

    fun onGuitarDriveChange(value: Float) {
        audioManager.updateParam { it.copy(guitarDrive = value) }
    }
}
