package com.vellarity.lightaccs.data.datasource

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProximityDataSource(
    private val sensorManager: SensorManager
){

    fun getProximityEvent(): Flow<SensorEvent> = callbackFlow {
        val listener: SensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

            override fun onSensorChanged(event: SensorEvent?) {
                event?.let{
                    when(it.sensor.type) {
                        Sensor.TYPE_PROXIMITY -> {trySend(it)}
                    }
                }
            }
        }

        val proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorManager.registerListener(listener, proximity, SensorManager.SENSOR_DELAY_NORMAL)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }


//    private fun handleProximity(event: SensorEvent) {
//        val maxRange = event.sensor.maximumRange
//        val isNear = event.values[0] < maxRange
//
//        if (isNear != isClose) {
//            isClose = isNear
//        }
//    }
}