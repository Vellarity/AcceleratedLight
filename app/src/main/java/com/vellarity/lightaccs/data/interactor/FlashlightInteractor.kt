package com.vellarity.lightaccs.data.interactor

import android.annotation.SuppressLint
import android.content.Context
import com.vellarity.lightaccs.data.service.SystemCameraManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FlashlightInteractor {
    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn = _isFlashOn.asStateFlow()

    @SuppressLint("StaticFieldLeak")
    private var systemCameraManager: SystemCameraManager? = null

    fun init(context: Context) {
        val applicationContext = context.applicationContext
        this.systemCameraManager = SystemCameraManager(applicationContext)
    }

    fun toggle() {
        val newState = !_isFlashOn.value
        _isFlashOn.value = newState
        systemCameraManager?.toggleFlash(state = newState)
    }
}