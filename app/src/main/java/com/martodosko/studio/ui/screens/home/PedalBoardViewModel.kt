package com.martodosko.studio.ui.screens.home

import androidx.lifecycle.ViewModel
import com.martodosko.studio.domain.state.AudioStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import javax.inject.Inject

@HiltViewModel
class PedalBoardViewModel @Inject constructor(
    private val audioManager: AudioStateManager
) : ViewModel() {

    // Expose states directly from the manager for observation
    val vocalState = audioManager.vocalState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val guitarState = audioManager.guitarState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Delegate updates to the manager
    fun onVocalGainChange(value: Float) = audioManager.updateVocalGain(value)
    fun onVocalVolumeChange(value: Float) = audioManager.updateVocalVolume(value)
    fun onGuitarGainChange(value: Float) = audioManager.updateGuitarGain(value)
    fun onGuitarVolumeChange(value: Float) = audioManager.updateGuitarVolume(value)
}
