package com.vellarity.lightaccs.data.repository

import android.hardware.SensorManager
import androidx.compose.runtime.mutableStateOf
import com.vellarity.lightaccs.data.service.SystemCameraManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

interface FlashlightRepository {
    var isFlash: StateFlow<Boolean>
    fun toggleFlash(state: Boolean)
}

class FlashlightRepositoryImpl(
    private val systemCameraManager: SystemCameraManager
): FlashlightRepository {

    var _isFlash: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override var isFlash: StateFlow<Boolean> = _isFlash.asStateFlow()

    override fun toggleFlash(state: Boolean) {
        systemCameraManager.toggleFlash(state=state)
        _isFlash.value = state
    }

}