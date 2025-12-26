package com.vellarity.lightaccs.data.service

import android.app.ForegroundServiceStartNotAllowedException
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.vellarity.lightaccs.MainActivity
import com.vellarity.lightaccs.data.interactor.FlashlightInteractor
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class LightAcceleratorService: Service(), SensorEventListener {
    private val TAG = "ShakeService"

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val SHAKE_THRESHOLD = 12.0f
    // Для логики детекции тряски (упрощенно)
    private var lastUpdateTime: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private val COOLDOWN_MS = 1500L
    private var lastToggleTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service: onCreate")
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service: onStartCommand")
        accelerometer?.also { sensor ->
            val registered = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Service: Listener registered: $registered")
        }
        return START_STICKY
    }

    private fun detectShake(x: Float, y: Float, z: Float) {
        val curTime = System.currentTimeMillis()

        if (curTime - lastToggleTime < COOLDOWN_MS) {
            return
        }

        val deltaX = abs(x - lastX)
        val deltaY = abs(y - lastY)
        val deltaZ = abs(z - lastZ)

        lastX = x
        lastY = y
        lastZ = z

        val HORIZONTAL_THRESHOLD = 15f
        val VERTICAL_NOISE_LIMIT = 4f

        val isHorizontalShake = deltaX > HORIZONTAL_THRESHOLD
        val isVerticalStable = deltaY < VERTICAL_NOISE_LIMIT && deltaZ < VERTICAL_NOISE_LIMIT

        if (isHorizontalShake && isVerticalStable) {
            Log.d(TAG, "HORIZONTAL SHAKE! Toggling light...")

            FlashlightInteractor.toggle()

            lastToggleTime = curTime
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                detectShake(it.values[0], it.values[1], it.values[2])
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}