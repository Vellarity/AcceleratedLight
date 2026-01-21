package com.vellarity.lightaccs.data.service

import android.app.Service
import android.content.Intent
import android.hardware.SensorEvent
import android.os.IBinder
import com.vellarity.lightaccs.data.repository.FlashlightRepository
import com.vellarity.lightaccs.data.repository.SensorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.abs

class LightAccelerometerService(
    private val sensorRepository: SensorRepository,
    private val flashlightRepository: FlashlightRepository
): Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var sensorJob: Job? = null


    private var isDim = false
    private var isClose = false
    private var isAccelerated = false
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private val COOLDOWN_MS = 1000L
    private var lastToggleTime: Long = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startProcessing()
        return START_STICKY
    }

    private fun startProcessing() {
        sensorJob?.cancel()

        sensorJob = serviceScope.launch {
            val lightFlow = sensorRepository.lightEvent
            val proximityFlow = sensorRepository.proximityEvent
            val accelerometerFlow = sensorRepository.accelerometerEvent

            combine(
                lightFlow,
                proximityFlow,
                accelerometerFlow
            ) { lightEvent, proximityEvent, accelerometerEvent ->
                handleProximity(proximityEvent)
                handleLight(lightEvent)
                handleAccelerometer(accelerometerEvent)
            }.collect {
                val curTime = System.currentTimeMillis()

                if (isClose && isDim) {
                    lastToggleTime = curTime
                    return@collect
                }

                if (curTime - lastToggleTime < COOLDOWN_MS) {
                    return@collect
                }

                if (isAccelerated) {
                    flashlightRepository.toggleFlash(!flashlightRepository.isFlash.value)
                    lastToggleTime = curTime
                }
            }
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

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val deltaX = abs(x - lastX)
        val deltaY = abs(y - lastY)
        val deltaZ = abs(z - lastZ)

        lastX = x
        lastY = y
        lastZ = z

        val HORIZONTAL_THRESHOLD = 14f
        val VERTICAL_NOISE_LIMIT = 4f

        val isHorizontalShake = deltaX > HORIZONTAL_THRESHOLD
        val isVerticalStable = deltaY < VERTICAL_NOISE_LIMIT && deltaZ < VERTICAL_NOISE_LIMIT

        isAccelerated = isHorizontalShake && isVerticalStable
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}