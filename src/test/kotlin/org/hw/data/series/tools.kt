package org.hw.data.series

import org.assertj.core.api.Assertions
import org.hw.data.series.storage.DataPoint
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

fun assertDataPoint(actual: DataPoint, expected: DataPoint) {
    Assertions.assertThat(actual.value).isEqualTo(expected.value)
    Assertions.assertThat(actual.timestamp).isEqualTo(expected.timestamp)
    Assertions.assertThat(actual.device).isEqualTo(expected.device)
    Assertions.assertThat(actual.user).isEqualTo(expected.user)
}

fun buildDataPoint(
    timestamp: Long = ThreadLocalRandom.current().nextLong(0, System.currentTimeMillis() / 1000),
    device: String = UUID.randomUUID().toString(),
    user: String = UUID.randomUUID().toString(),
    value: Double = ThreadLocalRandom.current().nextDouble()
) = DataPoint(
    timestamp = timestamp,
    device = device,
    user = user,
    value = value
)