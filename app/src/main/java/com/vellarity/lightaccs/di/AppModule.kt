package com.vellarity.lightaccs.di

import android.content.Context
import android.hardware.SensorManager
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vellarity.lightaccs.data.datasource.AccelerometerDataSource
import com.vellarity.lightaccs.data.datasource.LightDataSource
import com.vellarity.lightaccs.data.datasource.ProximityDataSource
import com.vellarity.lightaccs.data.datasource.SettingsDataSource
import com.vellarity.lightaccs.data.datasource.SettingsDataSourceImpl
import com.vellarity.lightaccs.data.repository.FlashlightRepository
import com.vellarity.lightaccs.data.repository.FlashlightRepositoryImpl
import com.vellarity.lightaccs.data.repository.SensorRepository
import com.vellarity.lightaccs.data.repository.SensorRepositoryImpl
import com.vellarity.lightaccs.data.repository.SettingsRepository
import com.vellarity.lightaccs.data.repository.SettingsRepositoryImpl
import com.vellarity.lightaccs.data.service.SystemCameraManager
import com.vellarity.lightaccs.data.service.SystemVibratorManager
import com.vellarity.lightaccs.data.usecase.InvokeServiceUseCase


class AppModule(
    private val context: Context
) {
    val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val settingsDataSource: SettingsDataSource = SettingsDataSourceImpl(context.datastore)

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometerDataSource: AccelerometerDataSource = AccelerometerDataSource(sensorManager)
    private val proximityDataSource: ProximityDataSource = ProximityDataSource(sensorManager)
    private val lightDataSource: LightDataSource = LightDataSource(sensorManager)

    private val cameraManager: SystemCameraManager = SystemCameraManager(context)

    val vibratorManager: SystemVibratorManager = SystemVibratorManager(context)
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(settingsDataSource)
    val flashlightRepository: FlashlightRepository = FlashlightRepositoryImpl(cameraManager)
    val sensorRepository: SensorRepository = SensorRepositoryImpl(
        accelerometerDataSource,
        proximityDataSource,
        lightDataSource
    )
    val invokeServiceUseCase: InvokeServiceUseCase = InvokeServiceUseCase(context, settingsRepository)
}