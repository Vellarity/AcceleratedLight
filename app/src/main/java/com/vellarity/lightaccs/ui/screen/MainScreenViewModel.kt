package com.vellarity.lightaccs.ui.screen

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Camera
import android.hardware.camera2.CameraManager
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vellarity.lightaccs.data.interactor.FlashlightInteractor
import com.vellarity.lightaccs.data.repository.FlashlightRepository
import com.vellarity.lightaccs.data.repository.SettingsRepository
import com.vellarity.lightaccs.data.service.LightAcceleratorService
import com.vellarity.lightaccs.data.service.SystemVibratorManager
import com.vellarity.lightaccs.data.usecase.InvokeServiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val flashlightRepository: FlashlightRepository,
    private val settingsRepository: SettingsRepository,
    private val vibratorManager: SystemVibratorManager,
    private val invokeServiceUseCase: InvokeServiceUseCase
): ViewModel() {
    private var _state: MutableStateFlow<MainScreenState> = MutableStateFlow(
        MainScreenState(
            isLight = false,
            isServiceActive = false,
            accelerationThreshold = 12f
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.isServiceActive.collect {
                _state.value.copy(isServiceActive = it)
            }

            settingsRepository.accelerateThreshold.collect {
                _state.value.copy(accelerationThreshold = it)
            }
        }
        _state.value.copy(isLight = flashlightRepository.isFlash.value)
    }

    fun onAction(action: MainScreenAction) {
        when (action) {
            is MainScreenAction.ToggleLight -> toggleLight()
            is MainScreenAction.ToggleService -> {}
        }
    }

    private fun toggleLight() {
        flashlightRepository.toggleFlash(!flashlightRepository.isFlash.value)
        vibratorManager.vibrate(400, 150)
        _state.value = _state.value.copy(
            isLight = flashlightRepository.isFlash.value
        )
    }
}