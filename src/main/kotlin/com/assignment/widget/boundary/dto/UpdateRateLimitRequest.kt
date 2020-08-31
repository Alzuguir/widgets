package com.assignment.widget.boundary.dto

import com.assignment.widget.boundary.ratelimit.BucketTypes

data class UpdateRateLimitRequest(
        val bucket: BucketTypes,
        val limit: Long
)