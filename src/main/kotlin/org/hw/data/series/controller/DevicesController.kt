package org.hw.data.series.controller

import org.hw.data.series.service.DataSeriesService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class DevicesController(
    private val dataSeriesService: DataSeriesService
) {

    /**
     * DELETE /devices/{device}/datapoints - delete all device datapoints
     */
    @RequestMapping(path = [deleteDeviceDataEndpoint])
    fun deleteDeviceDataPoints(@PathVariable device: String) {
        dataSeriesService.deleteDeviceDataPoints(device)
    }

    companion object {
        const val deleteDeviceDataEndpoint = "/devices/{device}/datapoints"
    }
}
