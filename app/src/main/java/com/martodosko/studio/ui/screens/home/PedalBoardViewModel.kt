// ==================================================
// FILE: PedalBoardViewModel.kt
// VERSION: v2.3.0-phase2-step2
// PURPOSE: Bridges UI Interactions with AudioStateManager
// NOTE: Injected via Hilt. Observes StateFlow from Manager.
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

    // Expose the current params as a StateFlow for the UI to collect
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
    
    // Add more handlers as needed for Volume, etc.
}
