package org.hw.data.series.service

import org.assertj.core.api.Assertions.assertThat
import org.hw.data.series.buildDataPoint
import org.junit.jupiter.api.Test
import java.time.Instant

internal class DataSeriesAverageCalculatorTest {

    @Test
    fun `calculate avg since oldest data`() {
        val now = Instant.now()
        val latestBucketData = listOf(buildDataPoint(timestamp = now.minusSeconds(5).epochSecond))
        val thirdLatestBucketData = listOf(
            buildDataPoint(timestamp = now.minusSeconds(2000).epochSecond),
            buildDataPoint(timestamp = now.minusSeconds(2005).epochSecond)
        )
        val data = latestBucketData + thirdLatestBucketData

        val averageValues = DataSeriesAverageCalculator.calculateAverage(data, null)

        assertThat(averageValues).hasSize(3)
        assertThat(averageValues[2]).isEqualByComparingTo(latestBucketData.first().value)
        assertThat(averageValues[1]).isEqualByComparingTo(0.0)
        assertThat(averageValues[0]).isEqualByComparingTo(thirdLatestBucketData.sumOf { it.value } / thirdLatestBucketData.size)
    }

    @Test
    fun `calculate avg given windowSize`() {
        val now = Instant.now()
        val latestBucketData = listOf(buildDataPoint(timestamp = now.minusSeconds(5).epochSecond))
        val thirdLatestBucketData = listOf(
            buildDataPoint(timestamp = now.minusSeconds(2000).epochSecond),
            buildDataPoint(timestamp = now.minusSeconds(2005).epochSecond)
        )
        val data = latestBucketData + thirdLatestBucketData

        val averageValues = DataSeriesAverageCalculator.calculateAverage(data, 2)

        assertThat(averageValues).hasSize(2)
        assertThat(averageValues[1]).isEqualByComparingTo(latestBucketData.first().value)
        assertThat(averageValues[0]).isEqualByComparingTo(0.0)
        //third bucket is not part of frame
    }
}