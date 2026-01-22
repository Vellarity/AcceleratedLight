package com.vellarity.lightaccs.data.service

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager

class SystemVibratorManager(private val context: Context) {

    private val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    private val vibrator = vibratorManager.defaultVibrator

    fun vibrate(time: Long, amplitude: Int) {
        if (vibrator.hasVibrator()) {
            val effect = VibrationEffect.createOneShot(time, amplitude)

            val attributes = android.media.AudioAttributes.Builder()
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            @Suppress("DEPRECATION")
            vibrator.vibrate(effect, attributes)
        }
    }


}