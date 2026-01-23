package com.vellarity.lightaccs.data.usecase

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.vellarity.lightaccs.data.repository.SettingsRepository
import com.vellarity.lightaccs.data.service.LightAccelerometerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InvokeServiceUseCase(
    private val appContext: Context,
    private val settingsRepository: SettingsRepository
) {

    enum class ServiceAction {
        START,
        STOP
    }

    // Потом переделаю, а пока посмотрю, как это работает.
    // Перегрузка операторов - отвратительное свойство языка
    operator fun invoke(action: ServiceAction) {
        val intent = Intent(appContext, LightAccelerometerService::class.java).apply {
            // Map the enum to the String action expected by the Service
            this.action = when (action) {
                ServiceAction.START -> LightAccelerometerService.Actions.START.name
                ServiceAction.STOP -> LightAccelerometerService.Actions.STOP.name
            }
        }

        when (action) {
            ServiceAction.START -> {
                // Use ContextCompat for safety across different Android versions
                ContextCompat.startForegroundService(appContext, intent)
                CoroutineScope(Dispatchers.IO).launch {
                    settingsRepository.setIsServiceActive(true)
                }
            }
            ServiceAction.STOP -> {
                appContext.stopService(intent)
                CoroutineScope(Dispatchers.IO).launch {
                    settingsRepository.setIsServiceActive(false)
                }
            }
        }
    }

}