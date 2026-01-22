package com.vellarity.lightaccs.data.repository

import android.hardware.SensorEvent
import com.vellarity.lightaccs.data.datasource.AccelerometerDataSource
import com.vellarity.lightaccs.data.datasource.LightDataSource
import com.vellarity.lightaccs.data.datasource.ProximityDataSource
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    val accelerometerEvent: Flow<SensorEvent>
    val lightEvent: Flow<SensorEvent>
    val proximityEvent: Flow<SensorEvent>
}

// Скорее-всего связывать несколько разных сервисов в один репозиторий неправильно,
// но нормальных примеров я не нашёл, так что покуда это никому не мешает...
class SensorRepositoryImpl(
    private val accelerometerDataSource: AccelerometerDataSource,
    private val proximityDataSource: ProximityDataSource,
    private val lightDataSource: LightDataSource
): SensorRepository {

    override val accelerometerEvent: Flow<SensorEvent>
        get() = accelerometerDataSource.getAccelerometerEvent()

    override val proximityEvent: Flow<SensorEvent>
        get() = proximityDataSource.getProximityEvent()

    override val lightEvent: Flow<SensorEvent>
        get() = lightDataSource.getLightEvent()

}