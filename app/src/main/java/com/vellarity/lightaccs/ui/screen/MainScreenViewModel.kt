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
import com.vellarity.lightaccs.data.service.LightAcceleratorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val context: Context
): ViewModel() {
    private var _state: MutableStateFlow<MainScreenState> = MutableStateFlow(
        MainScreenState(
            isLight = false,
            isShakeOn = false,
        )
    )
    val state = _state.asStateFlow()

    init {
        FlashlightInteractor.init(context)

        val serviceIntent = Intent(context, LightAcceleratorService::class.java)
        context.startForegroundService(serviceIntent)

        viewModelScope.launch {
            FlashlightInteractor.isFlashOn.collect { isLight ->
                _state.value = _state.value.copy(isLight = isLight)
            }
        }
    }

    fun onAction(action: MainScreenAction) {
        when (action) {
            is MainScreenAction.ToggleLight -> toggleLight()
            is MainScreenAction.ToggleService -> {}
        }
    }

    private fun toggleLight() {
        FlashlightInteractor.toggle()

        _state.value = _state.value.copy(
            isLight = FlashlightInteractor.isFlashOn.value
        )
    }

}