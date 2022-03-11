package org.hw.data.series.service

import org.hw.data.series.storage.DataPoint
import org.hw.data.series.storage.InMemoryDataSeriesStorage
import org.springframework.stereotype.Component

@Component
class DataSeriesService(
    private val storage: InMemoryDataSeriesStorage
) {
    fun storeDatapoint(dataPoint: DataPoint): Boolean {
        return storage.tryStore(dataPoint)
    }

    fun deleteDeviceDataPoints(device: String) {
        storage.deleteDeviceData(device)
    }

    fun deleteUserData(user: String) {
        storage.deleteUserData(user)
    }

    fun averageByDeviceSince(device: String, windowSize: Int?): List<Double> {
        return storage.deviceData(device).calculateAverage(windowSize)
    }

    fun averageByUserSince(user: String, windowSize: Int?): List<Double> {
        return storage.userData(user).calculateAverage(windowSize)
    }

    private fun Collection<DataPoint>?.calculateAverage(windowSize: Int?): List<Double> {
        return DataSeriesAverageCalculator.calculateAverage(this, windowSize)
    }
}