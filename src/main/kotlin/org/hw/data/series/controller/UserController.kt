package org.hw.data.series.controller

import org.hw.data.series.service.DataSeriesService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class UserController(
    private val dataSeriesService: DataSeriesService
) {

    /**
     * DELETE /users/{user}/datapoints - delete all user datapoints
     */
    @DeleteMapping(path = ["/users/{user}/datapoints"])
    fun deleteUserData(@PathVariable user: String) {
        dataSeriesService.deleteUserData(user)
    }
}