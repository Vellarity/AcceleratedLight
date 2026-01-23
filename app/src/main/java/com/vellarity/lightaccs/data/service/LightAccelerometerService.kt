package com.vellarity.lightaccs.data.service

import android.app.ForegroundServiceStartNotAllowedException
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.SensorEvent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.vellarity.lightaccs.LightAcceleratorApp
import com.vellarity.lightaccs.MainActivity
import com.vellarity.lightaccs.R
import com.vellarity.lightaccs.data.repository.FlashlightRepository
import com.vellarity.lightaccs.data.repository.SensorRepository
import com.vellarity.lightaccs.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.math.abs

class LightAccelerometerService: Service() {

    val TAG = "SERVICE"

    enum class Actions {
        START,
        STOP
    }

    private lateinit var sensorRepository: SensorRepository
    private lateinit var flashlightRepository: FlashlightRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var vibratorManager: SystemVibratorManager

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        // Не уверен, что ServiceObserver это хорошее решение, но лучшего я не нашёл
        sensorRepository = LightAcceleratorApp.appModule.sensorRepository
        flashlightRepository = LightAcceleratorApp.appModule.flashlightRepository
        settingsRepository = LightAcceleratorApp.appModule.settingsRepository
        vibratorManager = LightAcceleratorApp.appModule.vibratorManager
    }

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "light_accelerometer_channel"
            val channelName = "light_accelerometer_channel"

            // ВАЖНО: Используйте IMPORTANCE_LOW или выше!
            val importance = NotificationManager.IMPORTANCE_LOW

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Сервис датчиков для работы фонарика"
                setSound(null, null)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startForeground() {
        try {
            val notificationIntent = Intent(this, MainActivity::class.java)
            notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(this, "light_accelerometer_channel")
                .setContentTitle("Трясучий фонарик")
                .setContentText("Слушает акселерометр")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()
            ServiceCompat.startForeground(
                this,
                100,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                } else {
                    0
                },
            )
        } catch (e: Exception) {
            if (e is ForegroundServiceStartNotAllowedException) {
                TODO("Добавить обработки ошибок по проекту")
            }

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        startProcessing()
        return START_STICKY
    }

    private fun stopForeground() {
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorJob?.cancel()
        serviceScope.cancel()
    }

    private fun startProcessing() {
        sensorJob?.cancel()

        sensorJob = serviceScope.launch {
            val lightFlow = sensorRepository.lightEvent
            val proximityFlow = sensorRepository.proximityEvent
            val accelerometerFlow = sensorRepository.accelerometerEvent
            val accelerateThreshold = settingsRepository.accelerateThreshold

            combine(
                lightFlow,
                proximityFlow,
                accelerometerFlow,
                accelerateThreshold
            ) { lightEvent, proximityEvent, accelerometerEvent, accelerateThreshold ->
//                Log.d(TAG, "accelerateThreshold: $accelerateThreshold")
                handleProximity(proximityEvent)
                handleLight(lightEvent)
                handleAccelerometer(accelerometerEvent, accelerateThreshold)
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
                    vibratorManager.vibrate(400, 150)
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

    private fun handleAccelerometer(event: SensorEvent, accelerateThreshold: Float): Boolean {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val deltaX = abs(x - lastX)
        val deltaY = abs(y - lastY)
        val deltaZ = abs(z - lastZ)

        lastX = x
        lastY = y
        lastZ = z

        val VERTICAL_NOISE_LIMIT = 4f

        val isHorizontalShake = deltaX > accelerateThreshold
        val isVerticalStable = deltaY < VERTICAL_NOISE_LIMIT && deltaZ < VERTICAL_NOISE_LIMIT

        isAccelerated = isHorizontalShake && isVerticalStable

        return isAccelerated
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}