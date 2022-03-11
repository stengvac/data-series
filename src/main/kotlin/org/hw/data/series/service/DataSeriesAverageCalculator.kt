package org.hw.data.series.service

import org.hw.data.series.storage.DataPoint
import java.time.Duration
import java.time.Instant

object DataSeriesAverageCalculator {
    private val bucketDurationSeconds = Duration.ofMinutes(15).toSeconds()

    /**
     * @param windowSize if null take all data obtained before currentTimeStamp else compute start timestamp from windowSize
     *
     * @return avg value per bucket
     */
    fun calculateAverage(dataSeries: Collection<DataPoint>?, windowSize: Int?): List<Double> {
        if (dataSeries.isNullOrEmpty()) return List(windowSize ?: 0) { 0.0 }

        val currentTimeStamp = Instant.now().epochSecond
        val sinceTimeStamp = if (windowSize == null) {
            dataSeries.minOf { it.timestamp }
        } else currentTimeStamp - windowSize * bucketDurationSeconds
        //filter data which are requested
        val range = sinceTimeStamp..currentTimeStamp

        return calculateAvgIntern(
            sortedByTimeStamp = dataSeries.filter { it.timestamp in range }.sortedBy { it.timestamp },
            buckets = windowSize ?: computeBuckets(sinceTimeStamp, currentTimeStamp),
            sinceTimeStamp = sinceTimeStamp
        )
    }

    private fun calculateAvgIntern(
        sortedByTimeStamp: List<DataPoint>,
        buckets: Int,
        sinceTimeStamp: Long
    ): List<Double> {
        val accumulator = ArrayList<Double>(buckets)
        var dataSeriesIndex = 0

        for (bucketIndex in 0 until buckets) {
            var valueSum = 0.0
            var samplesInBucket = 0
            val currentBucketRange = sinceTimeStamp.toBucketRange(bucketIndex)
            for (currentDataSeriesIndex in dataSeriesIndex until sortedByTimeStamp.size) {
                val dataPoint = sortedByTimeStamp[currentDataSeriesIndex]
                if (dataPoint.timestamp !in currentBucketRange) break

                samplesInBucket++
                valueSum += dataPoint.value
            }
            dataSeriesIndex += samplesInBucket
            val bucketAvg = when {
                samplesInBucket > 0 -> valueSum / samplesInBucket
                else -> 0.0
            }
            accumulator.add(bucketAvg)
        }

        return accumulator
    }

    private fun Long.toBucketRange(bucketIndexSinceStart: Int): LongRange {
        val startTimeStamp = this + (bucketIndexSinceStart * bucketDurationSeconds)

        return startTimeStamp..(startTimeStamp + bucketDurationSeconds)
    }

    private fun computeBuckets(start: Long, currentTime: Long): Int {
        val wholeTimeFrame = currentTime - start
        val partialFrame = if (wholeTimeFrame % bucketDurationSeconds == 0L) 0 else 1

        return (wholeTimeFrame / bucketDurationSeconds + partialFrame).toInt()
    }
}