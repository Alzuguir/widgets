package com.assignment.widget.boundary.controller

import com.assignment.widget.WidgetTestFixtures
import com.assignment.widget.boundary.AbstractIntegrationTest
import com.assignment.widget.boundary.dto.UpdateRateLimitRequest
import com.assignment.widget.boundary.ratelimit.BucketTypes
import com.assignment.widget.boundary.ratelimit.RateLimiter
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class RatelimitControllerTest : AbstractIntegrationTest() {

    @Test
    @Order(2)
    fun `Should update list request limits`() {
        // GIVEN
        val newLimit = 300L
        val request = UpdateRateLimitRequest(BucketTypes.LIST, newLimit)

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/ratelimits")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))


        //THEN
        mvcResult.andExpect(status().isNoContent)
        assertThat(RateLimiter.listRateLimit).isEqualTo(newLimit)
    }

    @Test
    @Order(3)
    fun `Should update default request limits`() {
        // GIVEN
        val newLimit = 2000L
        val request = UpdateRateLimitRequest(BucketTypes.DEFAULT, newLimit)

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/ratelimits")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))


        //THEN
        mvcResult.andExpect(status().isNoContent)
        assertThat(RateLimiter.defaultRateLimit).isEqualTo(newLimit)
    }

    @Test
    @Order(1)
    fun `Should receive too many requests response after limit exceeds`() {
        // GIVEN
        val newLimit = 1L
        val widgetId = 1L
        val request = UpdateRateLimitRequest(BucketTypes.DEFAULT, newLimit)

        whenever(widgetRepository.getOne(widgetId)).thenReturn(WidgetTestFixtures.createWidget(widgetId, 0))

        // WHEN
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/ratelimits/")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))
                .andExpect(status().isNoContent)

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/widgets/{id}", widgetId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/widgets/{id}", widgetId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

                //THEN
                .andExpect(status().isTooManyRequests)
    }
}