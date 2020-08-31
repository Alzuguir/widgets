package com.assignment.widget.boundary.controller

import com.assignment.widget.boundary.dto.UpdateRateLimitRequest
import com.assignment.widget.boundary.ratelimit.RateLimiter
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ratelimits")
class RateLimitController {

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateDefaultLimits(@RequestBody request: UpdateRateLimitRequest): Unit =
            RateLimiter.refreshBucket(request)

}