// ==================================================
// FILE: PedalBoardViewModel.kt
// VERSION: v2.3.0-full-control-sync
// PURPOSE: Bridges Pedal Board Interactions with AudioStateManager
// NOTE: Added handlers for all pedal controls to ensure bidirectional sync.
// ==================================================
package com.martodosko.studio.ui.screens.home

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
class PedalBoardViewModel @Inject constructor(
    private val audioManager: AudioStateManager
) : ViewModel() {

    val audioParams: StateFlow<AudioParams> = audioManager.params
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AudioParams()
        )

    fun onVocalGainChange(value: Float) {
        audioManager.updateParam { it.copy(vocalGain = value) }
    }

    fun onGuitarDriveChange(value: Float) {
        audioManager.updateParam { it.copy(guitarDrive = value) }
    }
    
    // Add these if you want the Board to control Volume too
    fun onVocalVolumeChange(value: Float) {
        audioManager.updateParam { it.copy(vocalVolume = value) }
    }

    fun onGuitarLevelChange(value: Float) {
        audioManager.updateParam { it.copy(guitarLevel = value) }
    }
}
