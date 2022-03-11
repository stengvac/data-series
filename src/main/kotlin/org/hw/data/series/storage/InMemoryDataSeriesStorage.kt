package org.hw.data.series.storage

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import javax.validation.constraints.NotBlank

@Component
class InMemoryDataSeriesStorage {

    private val backingStorageUnique = ConcurrentHashMap<DataPoint, DataPoint>()
    private val byUserData = ConcurrentHashMap<String, ConcurrentLinkedQueue<DataPoint>>()
    private val byDeviceData = ConcurrentHashMap<String, ConcurrentLinkedQueue<DataPoint>>()

    //lets try whether data are unique. if not -> return false
    fun tryStore(dataPoint: DataPoint): Boolean {
        //putIfAbsent returning null is what I need -> those data are unique
        val stored = backingStorageUnique.putIfAbsent(dataPoint, dataPoint) == null

        if (stored) {
            //now is safe to add data also to other collections
            byUserData.getOrPut(dataPoint.user) { ConcurrentLinkedQueue() }.add(dataPoint)
            byDeviceData.getOrPut(dataPoint.device) { ConcurrentLinkedQueue() }.add(dataPoint)

        }

        return stored
    }

    fun deviceData(device: String): ConcurrentLinkedQueue<DataPoint>? = byDeviceData[device]
    fun userData(user: String): ConcurrentLinkedQueue<DataPoint>? = byUserData[user]

    fun deleteUserData(user: String) {
        byUserData.remove(user)?.forEach { data ->
            backingStorageUnique.remove(data)
            byDeviceData[data.device]?.remove(data)
        }
    }

    fun deleteDeviceData(device: String) {
        byDeviceData.remove(device)?.forEach { data ->
            backingStorageUnique.remove(data)
            byUserData[data.user]?.remove(data)
        }
    }
}

class DataPoint(
    val timestamp: Long,
    val value: Double,
    @field: NotBlank
    val device: String,
    @field: NotBlank
    val user: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataPoint

        if (timestamp != other.timestamp) return false
        if (device != other.device) return false
        if (user != other.user) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + device.hashCode()
        result = 31 * result + user.hashCode()
        return result
    }
}
