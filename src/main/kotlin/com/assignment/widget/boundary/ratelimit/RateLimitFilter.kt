package com.assignment.widget.boundary.ratelimit

import io.github.bucket4j.Bucket
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RateLimitFilter : OncePerRequestFilter() {

    val baseUri: String = "/widgets"

    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, fc: FilterChain) {
        when {
            req.method == HttpMethod.GET.name && req.requestURI == baseUri -> {
                res.addHeader("X-Rate-Limit", RateLimiter.listRateLimit.toString())
                handleTokenConsumption(RateLimiter.listBucket, res)
            }

            req.requestURI.contains(baseUri) -> {
                res.addHeader("X-Rate-Limit", RateLimiter.defaultRateLimit.toString())
                handleTokenConsumption(RateLimiter.defaultBucket, res)
            }
        }
        fc.doFilter(req, res)
    }

    private fun handleTokenConsumption(bucket: Bucket, res: HttpServletResponse) {
        val probe = bucket.tryConsumeAndReturnRemaining(1)
        if (probe.isConsumed) {
            res.addHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
        } else {
            res.addHeader("X-Rate-Limit-Retry-After-Seconds", probe.nanosToWaitForRefill.div(1000000000).toString())
            res.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Too many requests, please try again after ${probe.nanosToWaitForRefill.div(1000000000)} seconds")
        }
    }

}
