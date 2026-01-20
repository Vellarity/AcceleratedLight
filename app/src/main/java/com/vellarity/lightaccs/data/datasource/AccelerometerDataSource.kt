package com.vellarity.lightaccs.data.datasource

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.vellarity.lightaccs.data.interactor.FlashlightInteractor
import kotlin.math.abs

class AccelerometerDataSource(
    private val sensorManager: SensorManager
): SensorEventListener {

    private var callback: (Float, Float, Float) -> Unit = { _, _, _ -> }

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

    fun startListening(callback: (Float, Float, Float) -> Unit) {
        this.callback = callback
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL, 0)
        }
        proximity?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        light?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
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
            FlashlightInteractor.toggle()

            lastToggleTime = curTime
        }
    }

    private fun handleProximity(event: SensorEvent) {

        val maxRange = event.sensor.maximumRange
        val isNear = event.values[0] < maxRange

        if (isNear != isClose) {
            isClose = isNear
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
                Sensor.TYPE_ACCELEROMETER -> callback(event.values[0], event.values[1], event.values[3])
                Sensor.TYPE_LIGHT ->  handleLight(it)
            }
        }
    }

//    fun onCreate() {
//        super.onCreate()
//        Log.d(TAG, "Service: onCreate")
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
//        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
//    }

}