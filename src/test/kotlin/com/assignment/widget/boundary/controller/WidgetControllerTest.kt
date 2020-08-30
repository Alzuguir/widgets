package com.assignment.widget.boundary.controller

import com.assignment.widget.WidgetTestFixtures
import com.assignment.widget.boundary.AbstractIntegrationTest
import com.assignment.widget.domain.domainobject.Widget
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort
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

        whenever(widgetRepository.findAll(Sort.by("zIndex"))).thenReturn(widgets)

        // WHEN
        val mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/widgets")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

        val response = mvcResult.andReturn().response.contentAsString

        //THEN
        mvcResult.andExpect(status().isOk)
        assertThat(response).contains("\"numberOfElements\":11")
        assertThat(response).contains("\"sorted\":true")
        assertThat(response).contains("\"size\":10")
        assertThat(response).contains("\"totalPages\":2")
    }

    @Test
    fun `Should filter widget area on get all paginated request`() {
        // GIVEN
        val widgetInAreaIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
        val widgetOutAreaIds = listOf(12L, 13L, 14L, 15L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val widgetsInArea = mutableListOf<Widget>()
        val widgetsOutArea = mutableListOf<Widget>()

        for (i in widgetInAreaIds.indices) {
            widgetsInArea.add(WidgetTestFixtures.createWidget(
                    id = widgetInAreaIds[i],
                    zIndex = zIndexes[i],
                    x = 50,
                    y = 50,
                    height = 20,
                    width = 20
            ))
        }

        for (i in widgetOutAreaIds.indices) {
            widgetsOutArea.add(WidgetTestFixtures.createWidget(
                    id = widgetOutAreaIds[i],
                    zIndex = zIndexes[i],
                    x = 50,
                    y = 50,
                    height = 300,
                    width = 300
            ))
        }

        val allWidgets = mutableListOf<Widget>()
        allWidgets.addAll(widgetsInArea)
        allWidgets.addAll(widgetsOutArea)

        whenever(widgetRepository.findAll(Sort.by("zIndex"))).thenReturn(allWidgets)

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
        assertThat(response).contains("\"numberOfElements\":7")
        assertThat(response).contains("\"sorted\":true")
        assertThat(response).contains("\"size\":2")
        assertThat(response).contains("\"totalPages\":4")
    }
}