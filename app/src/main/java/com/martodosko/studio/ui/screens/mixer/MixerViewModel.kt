// ==================================================
// FILE: MixerViewModel.kt
// VERSION: v2.3.0-phase2-step3
// PURPOSE: Bridges Mixer UI with AudioStateManager
// NOTE: Shares the same singleton state as PedalBoardViewModel.
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

    fun onVocalVolumeChange(value: Float) {
        audioManager.updateParam { it.copy(vocalVolume = value) }
    }

    fun onGuitarLevelChange(value: Float) {
        audioManager.updateParam { it.copy(guitarLevel = value) }
    }
    
    fun onMasterVolumeChange(value: Float) {
        audioManager.updateParam { it.copy(masterVolume = value) }
    }
}
