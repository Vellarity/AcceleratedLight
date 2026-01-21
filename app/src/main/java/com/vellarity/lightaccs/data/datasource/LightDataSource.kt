package com.vellarity.lightaccs.data.datasource

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LightDataSource(
    private val sensorManager: SensorManager
) {

    fun getLightEvent(): Flow<SensorEvent> = callbackFlow {
        val listener: SensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let{
                    when(it.sensor.type) {
                        Sensor.TYPE_LIGHT -> {trySend(it)}
                    }
                }
            }
        }

        val proximity = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(listener, proximity, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

}