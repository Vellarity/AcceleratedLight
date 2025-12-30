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
    private var proximity: Sensor? = null
    private var light: Sensor? = null

    private var isClose = false
    private var isDim = false

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private val COOLDOWN_MS = 1000L
    private var lastToggleTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service: onCreate")
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service: onStartCommand")
        accelerometer?.also { sensor ->
            val registered = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL, 0)
            Log.d(TAG, "Service: Listener registered: $registered")
        }
        proximity?.also { sensor ->
            val registered = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Service: Listener registered: $registered")
        }
        light?.also { sensor ->
            val registered = sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d(TAG, "Service: Listener registered: $registered")
        }
        return START_STICKY
    }
    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val curTime = System.currentTimeMillis()

        val deltaX = abs(x - lastX)
        val deltaY = abs(y - lastY)
        val deltaZ = abs(z - lastZ)

        lastX = x
        lastY = y
        lastZ = z

        if (isClose && isDim) {
            lastToggleTime = curTime
            return
        }

        if (curTime - lastToggleTime < COOLDOWN_MS) {
            return
        }

        val HORIZONTAL_THRESHOLD = 14f
        val VERTICAL_NOISE_LIMIT = 4f

        val isHorizontalShake = deltaX > HORIZONTAL_THRESHOLD
        val isVerticalStable = deltaY < VERTICAL_NOISE_LIMIT && deltaZ < VERTICAL_NOISE_LIMIT

        if (isHorizontalShake && isVerticalStable) {
            Log.d(TAG, "HORIZONTAL SHAKE! Toggling light...")

            FlashlightInteractor.toggle()

            lastToggleTime = curTime
        }
    }

    private fun handleProximity(event: SensorEvent) {

        val maxRange = event.sensor.maximumRange
        val isNear = event.values[0] < maxRange

        if (isNear != isClose) {
            isClose = isNear
            Log.d(TAG, "Proximity changed: In Pocket = $isClose")
        }
    }

    private fun handleLight(event: SensorEvent) {
        val lightLevel = event.values[0]
        if (lightLevel < 5) {
            isDim = true
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_PROXIMITY -> handleProximity(it)
                Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(it)
                Sensor.TYPE_LIGHT ->  handleLight(it)
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