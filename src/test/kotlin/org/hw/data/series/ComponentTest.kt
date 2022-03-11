package org.hw.data.series

import org.assertj.core.api.Assertions.assertThat
import org.hw.data.series.controller.AverageDataSeriesResponse
import org.hw.data.series.controller.DataSeriesController
import org.hw.data.series.controller.DevicesController
import org.hw.data.series.controller.StatisticsController
import org.hw.data.series.storage.DataPoint
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.util.UriComponentsBuilder
import java.time.Instant

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ComponentTest(
    context: AbstractApplicationContext
) {
    private final val testRestTemplate: TestRestTemplate = context.getBean()

    init {
        //by default test rest template does not throw any ex on error -_-
        testRestTemplate.restTemplate.errorHandler = DefaultResponseErrorHandler()
    }

    /**
     * Yes this test should normally be split into more independent tests, but this is basically example. So pardon me
     */
    @Test
    fun `device endpoints work as expected`() {
        val device = "device"
        val beforeTheLast = Instant.now().minusSeconds(1500).epochSecond
        val dataPoint = addDataPoint(buildDataPoint(device = device, timestamp = beforeTheLast))
        val secondDataPoint = addDataPoint(buildDataPoint(device = device, timestamp = beforeTheLast))
        val expectedAvg = (dataPoint.value + secondDataPoint.value) / 2

        val deviceData = restGetCall<AverageDataSeriesResponse>(
            StatisticsController.averageByDeviceSinceStartEndpoint,
            device
        )

        assertThat(deviceData.data).hasSize(2)
        assertThat(deviceData.data.first()).isEqualByComparingTo(expectedAvg)
        assertThat(deviceData.data[1]).isEqualByComparingTo(0.0)

        val movingWindowAvgData = restGetCall<AverageDataSeriesResponse>(
            StatisticsController.averageByDeviceMovingAvgEndpoint,
            device,
            mapOf("window_size" to 3)
        )

        assertThat(movingWindowAvgData.data).hasSize(3)
        assertThat(movingWindowAvgData.data[0]).isEqualByComparingTo(0.0)
        assertThat(movingWindowAvgData.data[1]).isEqualByComparingTo(expectedAvg)
        assertThat(movingWindowAvgData.data[2]).isEqualByComparingTo(0.0)

        deleteRestCall(DevicesController.deleteDeviceDataEndpoint, device)

        val afterDeleteDeviceData = restGetCall<AverageDataSeriesResponse>(
            StatisticsController.averageByDeviceSinceStartEndpoint,
            device
        )
        assertThat(afterDeleteDeviceData.data).isEmpty()
    }

    fun addDataPoint(dataPoint: DataPoint = buildDataPoint()): DataPoint {
        testRestTemplate.postForEntity(DataSeriesController.dataPointsEndpoint, dataPoint, Unit::class.java)

        return dataPoint
    }

    private inline fun <reified T> restGetCall(
        path: String,
        pathVariable: String,
        query: Map<String, Any> = mapOf()
    ): T {
        val pathWithQuery = UriComponentsBuilder.newInstance().apply {
            query.forEach { (query, value) -> queryParam(query, value) }
        }.toUriString()

        return testRestTemplate.exchange(
            path + pathWithQuery,
            HttpMethod.GET,
            HttpEntity<Unit>(null, HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }),
            T::class.java,
            pathVariable
        ).body!!
    }

    private fun deleteRestCall(
        path: String,
        vararg pathVariables: String
    ) {
        testRestTemplate.exchange(
            path,
            HttpMethod.DELETE,
            HttpEntity<Unit>(null, HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }),
            Unit::class.java,
            *pathVariables
        )
    }
}