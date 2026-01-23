package com.vellarity.lightaccs.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vellarity.lightaccs.data.repository.FlashlightRepository
import com.vellarity.lightaccs.data.repository.SettingsRepository
import com.vellarity.lightaccs.data.service.SystemVibratorManager
import com.vellarity.lightaccs.data.usecase.InvokeServiceUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainScreenViewModel(
    private val flashlightRepository: FlashlightRepository,
    private val settingsRepository: SettingsRepository,
    private val vibratorManager: SystemVibratorManager,
    private val invokeServiceUseCase: InvokeServiceUseCase
): ViewModel() {

    val state = combine(
        flashlightRepository.isFlash,
        settingsRepository.isServiceActive,
        settingsRepository.accelerateThreshold) {
        isFlash, isServiceActive, accelerateThreshold ->
        MainScreenState(
            isLight = isFlash,
            isServiceActive = isServiceActive,
            accelerationThreshold = accelerateThreshold
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainScreenState()
    )

    fun onAction(action: MainScreenAction) {
        when (action) {
            is MainScreenAction.ToggleLight -> toggleLight()
            is MainScreenAction.ToggleService -> {}
        }
    }

    private fun toggleLight() {
        flashlightRepository.toggleFlash(!flashlightRepository.isFlash.value)
        vibratorManager.vibrate(400, 150)
    }
}