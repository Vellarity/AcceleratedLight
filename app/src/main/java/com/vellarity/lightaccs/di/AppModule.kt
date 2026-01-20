package com.vellarity.lightaccs.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vellarity.lightaccs.data.datasource.SettingsDataSource
import com.vellarity.lightaccs.data.datasource.SettingsDataSourceImpl
import com.vellarity.lightaccs.data.repository.SettingsRepository
import com.vellarity.lightaccs.data.repository.SettingsRepositoryImpl
import com.vellarity.lightaccs.data.service.SystemCameraManager


class AppModule(
    private val context: Context
) {
    val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val settingsDataSource: SettingsDataSource = SettingsDataSourceImpl(context.datastore)
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(settingsDataSource)

    val cameraManager = SystemCameraManager(context)
}