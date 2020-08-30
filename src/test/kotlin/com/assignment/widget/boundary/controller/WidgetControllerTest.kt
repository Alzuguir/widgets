package com.assignment.widget.boundary.controller

import com.assignment.widget.WidgetTestFixtures
import com.assignment.widget.boundary.AbstractIntegrationTest
import com.assignment.widget.domain.domainobject.Widget
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class WidgetControllerTest : AbstractIntegrationTest() {

    @Test
    fun `Should get Widget By Id`() {
        // GIVEN
        val widgetId = 1L
        val zIndex = 0

        whenever(widgetRepository.getOne(widgetId)).thenReturn(WidgetTestFixtures.createWidget(widgetId, zIndex))

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/widgets/{id}", widgetId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

        val response = gson.fromJson(mvcResult.andReturn().response.contentAsString, Widget::class.java)

        //THEN
        mvcResult.andExpect(status().isOk)
        assertThat(response.id).isEqualTo(widgetId)
        assertThat(response.zIndex).isEqualTo(zIndex)
    }

    @Test
    fun `Should create widget successfully`() {
        // GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)
        val request = WidgetTestFixtures.createWidgetRequest(widget)

        whenever(widgetRepository.save(any<Widget>())).thenReturn(widget)

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/widgets")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))

        val response = gson.fromJson(mvcResult.andReturn().response.contentAsString, Widget::class.java)

        //THEN
        mvcResult.andExpect(status().isCreated)
        assertThat(response.id).isEqualTo(widgetId)
        assertThat(response.zIndex).isEqualTo(zIndex)
    }

    @Test
    fun `Should update widget successfully`() {
        // GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)
        val request = WidgetTestFixtures.updateWidgetRequest(widget)

        whenever(widgetRepository.save(any<Widget>())).thenReturn(widget)
        whenever(widgetRepository.getOne(widgetId)).thenReturn(widget)

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .put("/widgets/{id}", widgetId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request)))

        val response = gson.fromJson(mvcResult.andReturn().response.contentAsString, Widget::class.java)

        //THEN
        mvcResult.andExpect(status().isAccepted)
        assertThat(response.id).isEqualTo(widgetId)
        assertThat(response.zIndex).isEqualTo(zIndex)
    }

    @Test
    fun `Should delete widget successfully`() {
        // GIVEN
        val widgetId = 1L

        doNothing().whenever(widgetRepository).deleteById(widgetId)

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/widgets/{id}", widgetId)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

        //THEN
        mvcResult.andExpect(status().isNoContent)
    }

    @Test
    fun `Should list widget with default pagination`() {
        // GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        whenever(widgetRepository.findAllPagedSorted(any())).thenReturn(PageImpl<Widget>(widgets))

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/widgets")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("sort", "zIndex,asc"))

        val response = mvcResult.andReturn().response.contentAsString

        //THEN
        mvcResult.andExpect(status().isOk)
        assertThat(response).contains("\"numberOfElements\":11")
        assertThat(response).contains("\"size\":11")
        assertThat(response).contains("\"totalPages\":1")
        verify(widgetRepository, times(1)).findAllPagedSorted(any())
    }

    @Test
    fun `Should filter widget area on get all paginated request`() {
        // GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        whenever(widgetRepository.findAllPagedFilteredSorted(any(), any(), any(), any(), any()))
                .thenReturn(PageImpl<Widget>(widgets))

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/widgets")
                        .param("lowerLeftX", "0")
                        .param("lowerLeftY", "0")
                        .param("upperRightX", "200")
                        .param("upperRightY", "200")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

        val response = mvcResult.andReturn().response.contentAsString

        //THEN
        mvcResult.andExpect(status().isOk)
        assertThat(response).contains("\"numberOfElements\":11")
        assertThat(response).contains("\"size\":11")
        assertThat(response).contains("\"totalPages\":1")
        verify(widgetRepository, times(1)).findAllPagedFilteredSorted(any(), any(), any(), any(), any())
    }
}