package com.vellarity.lightaccs.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


interface SettingsDataSource {
    suspend fun setIsServiceActive(isActive: Boolean)
    suspend fun setAccelerateThreshold(accelerateThreshold: Float)
    fun isServiceActive(): Flow<Boolean>
    fun accelerateThreshold(): Flow<Float>
}


class SettingsDataSourceImpl(private val dataStore: DataStore<Preferences>): SettingsDataSource {
//    val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val IS_SERVICE_ACTIVE = booleanPreferencesKey(name="IS_SERVICE_ACTIVE")
    val ACCELERATE_THRESHOLD = floatPreferencesKey(name = "ACCELERATE_THRESHOLD")

    override suspend fun setIsServiceActive(isActive: Boolean) {
        dataStore.edit {
            it[IS_SERVICE_ACTIVE] = isActive
        }
    }

    override suspend fun setAccelerateThreshold(accelerateThreshold: Float) {
        dataStore.edit {
            it[ACCELERATE_THRESHOLD] = accelerateThreshold
        }
    }

    override fun isServiceActive(): Flow<Boolean> = dataStore.data.map {preferences ->
        preferences[IS_SERVICE_ACTIVE] ?: false
    }

    override fun accelerateThreshold(): Flow<Float> = dataStore.data.map {preferences ->
        preferences[ACCELERATE_THRESHOLD] ?: 12f
    }
}