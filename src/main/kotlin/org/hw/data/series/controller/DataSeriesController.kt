package org.hw.data.series.controller

import org.hw.data.series.service.DataSeriesService
import org.hw.data.series.storage.DataPoint
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


/**
 * Write a rest server to track a data time series.
 * Implement endpoints described below with appropriate response statuses.
 * The server should keep the data only in memory, no persistence is necessary, database (such as H2) is not allowed.
 */
@Validated
@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class DataSeriesController(
    private val dataSeriesService: DataSeriesService
) {

    /**
     * POST should accept json object with keys described in brackets (all mandatory fields)
     * POST /datapoints [timestamp;value;device;user] - add new datapoint to time series. Tuple {timestamp, device, user} is unique.
     * Adding the same point should cause bad request response and not change the data.
     */
    @PostMapping(path = [dataPointsEndpoint])
    fun storeDatapoint(@RequestBody @Valid dataPoint: DataPoint): ResponseEntity<Unit> {
        val stored = dataSeriesService.storeDatapoint(dataPoint)

        return if (stored) {
            noContentResponseEntity
        } else badRequest
    }

    companion object {
        private val noContentResponseEntity = ResponseEntity.noContent().build<Unit>()
        private val badRequest = ResponseEntity.badRequest().build<Unit>()

        const val dataPointsEndpoint = "/datapoints"
    }
}
