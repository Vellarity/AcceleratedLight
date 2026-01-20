package com.vellarity.lightaccs.data.service

import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraManager

class SystemCameraManager(private val context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    fun toggleFlash(cameraID: String = "0", state: Boolean) {
        try {
            cameraManager.setTorchMode(cameraID, state)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}