package com.assignment.widget.boundary.ratelimit

import com.assignment.widget.boundary.dto.UpdateRateLimitRequest
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Bucket4j
import io.github.bucket4j.Refill
import java.time.Duration
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write


object RateLimiter {

    private val readWriteLock = ReentrantReadWriteLock()

    var listRateLimit = 200L
    var listBucket: Bucket = Bucket4j.builder()
            .addLimit(Bandwidth.classic(listRateLimit, Refill.intervally(listRateLimit, Duration.ofMinutes(1))))
            .build()

    var defaultRateLimit = 1000L
    var defaultBucket: Bucket = Bucket4j.builder()
            .addLimit(Bandwidth.classic(defaultRateLimit, Refill.intervally(defaultRateLimit, Duration.ofMinutes(1))))
            .build()

    fun refreshBucket(request: UpdateRateLimitRequest) {
        when (request.bucket) {
            BucketTypes.DEFAULT -> refreshDefaultBucket(request.limit)
            BucketTypes.LIST -> refreshListBucket(request.limit)
        }
    }

    private fun refreshListBucket(limit: Long) {
        readWriteLock.write {
            listRateLimit = limit
            listBucket = Bucket4j.builder()
                    .addLimit(Bandwidth.classic(limit, Refill.intervally(limit, Duration.ofMinutes(1))))
                    .build()
        }
    }

    private fun refreshDefaultBucket(limit: Long) {
        readWriteLock.write {
            defaultRateLimit = limit
            defaultBucket = Bucket4j.builder()
                    .addLimit(Bandwidth.classic(limit, Refill.intervally(limit, Duration.ofMinutes(1))))
                    .build()
        }
    }
}