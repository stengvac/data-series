package org.hw.data.series.controller

import org.hw.data.series.service.DataSeriesService
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Validated
@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class StatisticsController(
    private val dataSeriesService: DataSeriesService
) {

    /**
     *  GET /statistics/devices/{device}/avg
     *
     *  return list of 15 minutes averages of time series from first datapoint to current time. Matching device key
     */
    @GetMapping(averageByDeviceSinceStartEndpoint)
    fun averageByDeviceSinceStart(@PathVariable @NotBlank device: String): AverageDataSeriesResponse {
        return dataSeriesService.averageByDeviceSince(device, null).toAverageDataSeriesResponse()
    }

    /**
     * GET /statistics/devices/{device}/moving_avg?window_size={window_size}
     *
     * return list of moving averages of 15 minutes average buckets. I.E. moving averages of result /statistics/devices/{device}/avg
     */
    @GetMapping(averageByDeviceMovingAvgEndpoint)
    fun averageByDeviceMovingAvg(
        @PathVariable @NotBlank device: String,
        @RequestParam(name = "window_size") @Min(1) @Max(100_000) windowSize: Int
    ): AverageDataSeriesResponse {
        return dataSeriesService.averageByDeviceSince(device, windowSize).toAverageDataSeriesResponse()
    }

    /**
     *  GET /statistics/users/{user}/avg
     *
     *  return list of 15 minutes averages of time series from first datapoint to current time. Matching user key
     */
    @GetMapping("/statistics/users/{user}/avg")
    fun averageByUserSinceStart(@PathVariable @NotBlank user: String): AverageDataSeriesResponse {
        return dataSeriesService.averageByUserSince(user, null).toAverageDataSeriesResponse()
    }

    /**
     * GET /statistics/users/{user}/moving_avg?window_size={window_size}
     *
     * return list of moving averages of 15 minutes average buckets. I.E. moving averages of result /statistics/devices/{device}/avg
     */
    @GetMapping("/statistics/users/{user}/moving_avg")
    fun averageByUserMovingWindow(
        @PathVariable @NotBlank user: String,
        @RequestParam(name = "window_size") @Min(1) @Max(100_000) windowSize: Int
    ): AverageDataSeriesResponse {
        return dataSeriesService.averageByUserSince(user, windowSize).toAverageDataSeriesResponse()
    }

    private fun List<Double>.toAverageDataSeriesResponse(): AverageDataSeriesResponse {
        return AverageDataSeriesResponse(this)
    }

    companion object {
        const val averageByDeviceSinceStartEndpoint = "/statistics/devices/{device}/avg"
        const val averageByDeviceMovingAvgEndpoint = "/statistics/devices/{device}/moving_avg"
    }
}

data class AverageDataSeriesResponse(
    val data: List<Double>
)