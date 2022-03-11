package org.hw.data.series.storage

import org.assertj.core.api.Assertions.assertThat
import org.hw.data.series.assertDataPoint
import org.hw.data.series.buildDataPoint
import org.junit.jupiter.api.Test

internal class InMemoryDataSeriesStorageTest {

    private val tested = InMemoryDataSeriesStorage()

    @Test
    fun `unique store works`() {
        val dataPoint = buildDataPoint()
        //true -> stored
        assertThat(tested.tryStore(dataPoint)).isTrue()
        //can retrieve data
        val userData = tested.userData(dataPoint.user)
        assertThat(userData).isNotNull().hasSize(1)
        assertDataPoint(userData!!.first(), dataPoint)

        val deviceData = tested.deviceData(dataPoint.device)
        assertThat(deviceData).isNotNull().hasSize(1)
        assertDataPoint(deviceData!!.first(), dataPoint)

        //already stored but has to be unique
        val sameKeyDiffValue = buildDataPoint(
            timestamp = dataPoint.timestamp,
            device = dataPoint.device,
            user = dataPoint.user,
        )
        assertThat(tested.tryStore(sameKeyDiffValue)).isFalse()
        //stored value is still old one
        val sameUserData = tested.userData(dataPoint.user)
        assertThat(sameUserData).isNotNull().hasSize(1)
        assertDataPoint(sameUserData!!.first(), dataPoint)

        val sameDeviceData = tested.deviceData(dataPoint.device)
        assertThat(sameDeviceData).isNotNull().hasSize(1)
        assertDataPoint(sameDeviceData!!.first(), dataPoint)
    }

    @Test
    fun `delete user data works fine`() {
        val user = "user"
        val firstData = buildDataPoint(user = user)
        val secondData = buildDataPoint(user = user)

        assertThat(tested.tryStore(firstData)).isTrue()
        assertThat(tested.tryStore(secondData)).isTrue()

        val userData = tested.userData(user)
        assertThat(userData).isNotNull().hasSize(2)
        assertThat(userData).contains(firstData)
        assertThat(userData).contains(secondData)

        tested.deleteUserData(user)

        assertThat(tested.userData(user)).isNull()
    }

    @Test
    fun `delete device data works fine`() {
        val device = "device"
        val firstData = buildDataPoint(device = device)
        val secondData = buildDataPoint(device = device)

        assertThat(tested.tryStore(firstData)).isTrue()
        assertThat(tested.tryStore(secondData)).isTrue()

        val deviceData = tested.deviceData(device)
        assertThat(deviceData).isNotNull().hasSize(2)
        assertThat(deviceData).contains(firstData)
        assertThat(deviceData).contains(secondData)

        tested.deleteDeviceData(device)

        assertThat(tested.deviceData(device)).isNull()
    }
}
