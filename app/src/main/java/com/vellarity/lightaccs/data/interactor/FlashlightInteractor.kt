package com.vellarity.lightaccs.data.interactor

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.vellarity.lightaccs.data.service.SystemCameraManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FlashlightInteractor {
    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn = _isFlashOn.asStateFlow()


    private lateinit var appContext: Context
    @SuppressLint("StaticFieldLeak")
    private var systemCameraManager: SystemCameraManager? = null

    fun init(application: Application) {
        this.systemCameraManager = SystemCameraManager(application)
    }

    fun toggle() {
        val newState = !_isFlashOn.value
        _isFlashOn.value = newState
        systemCameraManager?.toggleFlash(state = newState)

        vibrate()
    }

    private fun vibrate() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            // Проверяем, поддерживает ли устройство вибрацию
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createOneShot(400, 150)

                    val attributes = android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()

                    @Suppress("DEPRECATION")
                    vibrator.vibrate(effect, attributes)
                } else {
                    // Для старых версий
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(1000)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}