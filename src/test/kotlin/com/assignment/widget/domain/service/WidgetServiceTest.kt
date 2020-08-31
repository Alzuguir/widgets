package com.assignment.widget.domain.service

import com.assignment.widget.WidgetTestFixtures
import com.assignment.widget.adapter.WidgetAdapter
import com.assignment.widget.boundary.dto.FilterWidgetsRequest
import com.assignment.widget.domain.domainobject.Widget
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

internal class WidgetServiceTest {

    private val widgetAdapter: WidgetAdapter = mock()
    private val widgetService = WidgetService(widgetAdapter)

    @Test
    fun `Should get widget by id`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0

        whenever(widgetAdapter.getWidgetById(widgetId)).thenReturn(WidgetTestFixtures.createWidget(widgetId, zIndex))

        //WHEN
        val widget = widgetService.getWidgetById(1L)

        //THEN
        assertThat(widget.id).isEqualTo(widgetId)
        assertThat(widget.zIndex).isEqualTo(zIndex)
    }

    @Test
    fun `Should create widget`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetAdapter.createWidget(any())).thenReturn(widget)

        //WHEN
        val createdWidget = widgetService.createWidget(WidgetTestFixtures.createWidgetRequest(widget))

        //THEN
        assertThat(createdWidget.id).isEqualTo(widget.id)
        assertThat(createdWidget.zIndex).isEqualTo(createdWidget.zIndex)
    }

    @Test
    fun `Should create widget with null zIndex on request`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetAdapter.createWidget(any())).thenReturn(widget)

        //WHEN
        val createdWidget = widgetService.createWidget(WidgetTestFixtures.createWidgetRequest(widget).copy(zIndex = null))

        //THEN
        assertThat(createdWidget.id).isEqualTo(widget.id)
        assertThat(createdWidget.zIndex).isEqualTo(createdWidget.zIndex)
        verify(widgetAdapter, times(1)).getLowestZIndex()
    }

    @Test
    fun `Should create widget and increment existent zIndexes when zIndex already exists`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetAdapter.createWidget(any())).thenReturn(widget)
        whenever(widgetAdapter.checkZIndexAlreadyExists(zIndex)).thenReturn(true)

        //WHEN
        widgetService.createWidget(WidgetTestFixtures.createWidgetRequest(widget))

        //THEN
        verify(widgetAdapter, times(1)).incrementHigherAndEqualZIndexes(zIndex)
    }

    @Test
    fun `Should update widget`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetAdapter.getWidgetById(any())).thenReturn(widget)
        whenever(widgetAdapter.updateWidget(any())).thenReturn(widget)

        //WHEN
        val updatedWidget = widgetService.updateWidget(WidgetTestFixtures.updateWidgetRequest(widget), widgetId)

        //THEN
        assertThat(updatedWidget.id).isEqualTo(widget.id)
        assertThat(updatedWidget.zIndex).isEqualTo(updatedWidget.zIndex)
    }

    @Test
    fun `Should update widget and increment zIndex when zIndex already exists`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetAdapter.getWidgetById(any())).thenReturn(widget)
        whenever(widgetAdapter.updateWidget(any())).thenReturn(widget)
        whenever(widgetAdapter.checkZIndexAlreadyExists(zIndex)).thenReturn(true)

        //WHEN
        val updatedWidget = widgetService.updateWidget(WidgetTestFixtures.updateWidgetRequest(widget), widgetId)

        //THEN
        assertThat(updatedWidget.id).isEqualTo(widget.id)
        assertThat(updatedWidget.zIndex).isEqualTo(updatedWidget.zIndex)
        verify(widgetAdapter, times(1)).incrementHigherAndEqualZIndexes(zIndex)
    }

    @Test
    fun `Should delete widget`() {
        //GIVEN
        val widgetId = 1L

        doNothing().whenever(widgetAdapter).deleteWidget(widgetId)

        //WHEN
        widgetService.deleteWidget(widgetId)

        //THEN
        verify(widgetAdapter, times(1)).deleteWidget(widgetId)
    }

    @Test
    fun `Should get All widgets`() {
        // GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        whenever(widgetAdapter.getAllWidgetsFilteredPagedSorted(any(), any())).thenReturn(
                PageImpl<Widget>(widgets, PageRequest.of(0, 10, Sort.Direction.ASC, "zIndex"), widgets.size.toLong()
                ))

        //WHEN
        val widgetsResult = widgetService.getAllWidgets(Pageable.unpaged(), FilterWidgetsRequest())

        //THEN
        assertThat(widgetsResult.content.size).isEqualTo(11)
    }
}