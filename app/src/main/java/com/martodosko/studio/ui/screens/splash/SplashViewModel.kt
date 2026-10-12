// ==================================================
// FILE: ui/screens/splash/SplashViewModel.kt
// VERSION: 2.1.0
// PURPOSE: Orchestrate Boot Sequence
// ==================================================
package com.martodosko.studio.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martodosko.studio.data.services.AssetSyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashUiState {
    object Loading : SplashUiState()
    data class Updating(val progress: Float, val message: String) : SplashUiState()
    object Ready : SplashUiState()
    data class Error(val message: String) : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val assetService: AssetSyncService
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        checkAndPrepareEngine()
    }

    private fun checkAndPrepareEngine() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading
            
            // 1. Fetch Metadata
            val result = assetService.fetchLatestAssets()
            
            result.fold(
                onSuccess = { assets ->
                    if (assets.isEmpty()) {
                        // No dynamic libs configured yet, proceed with bundled ones
                        _uiState.value = SplashUiState.Ready
                        return@fold
                    }

                    // 2. Check each critical asset
                    var totalSize = 0L
                    var downloadedSize = 0L
                    
                    assets.forEach { asset ->
                        if (assetService.isUpdateRequired(asset)) {
                            _uiState.value = SplashUiState.Updating(
                                progress = 0f, 
                                message = "Preparing audio engine..."
                            )
                            
                            val dlResult = assetService.downloadAsset(asset)
                            dlResult.onFailure { e ->
                                _uiState.value = SplashUiState.Error("Failed to load engine: ${e.message}")
                                return@launch
                            }
                        }
                    }
                    
                    // 3. All good
                    _uiState.value = SplashUiState.Ready
                },
                onFailure = { e ->
                    // Network error? Fallback to bundled/default or show retry
                    _uiState.value = SplashUiState.Error("Connection lost: ${e.localizedMessage}")
                }
            )
        }
    }
}
