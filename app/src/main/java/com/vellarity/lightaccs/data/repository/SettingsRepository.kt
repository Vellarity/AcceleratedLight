package com.vellarity.lightaccs.data.repository

import androidx.datastore.preferences.core.edit
import com.vellarity.lightaccs.data.datasource.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface SettingsRepository{
    suspend fun setIsServiceActive(isActive: Boolean)
    suspend fun setAccelerateThreshold(accelerateThreshold: Float)
    val isServiceActive: Flow<Boolean>
    val accelerateThreshold: Flow<Float>
}

class SettingsRepositoryImpl(
    private val settingsDataSource: SettingsDataSource
): SettingsRepository {

    override val isServiceActive: Flow<Boolean> = settingsDataSource.isServiceActive()
    override val accelerateThreshold: Flow<Float> = settingsDataSource.accelerateThreshold()


    override suspend fun setIsServiceActive(isActive: Boolean) {
        settingsDataSource.setIsServiceActive(isActive)
    }

    override suspend fun setAccelerateThreshold(accelerateThreshold: Float) {
        settingsDataSource.setAccelerateThreshold(accelerateThreshold)
    }

}