package com.assignment.widget.adapter

import com.assignment.widget.WidgetTestFixtures
import com.assignment.widget.adapter.memorystorage.WidgetMemoryStorage
import com.assignment.widget.adapter.repository.WidgetRepository
import com.assignment.widget.boundary.dto.FilterWidgetsRequest
import com.assignment.widget.domain.domainobject.Widget
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.util.ReflectionTestUtils

internal class WidgetAdapterTest {

    private val memoryStorage: WidgetMemoryStorage = mock()
    private val widgetRepository: WidgetRepository = mock()
    private val widgetAdapter = WidgetAdapter(memoryStorage, widgetRepository)

    @Test
    fun `should create Widget in DB`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetRepository.save(widget)).thenReturn(widget)

        //WHEN
        val result = widgetAdapter.createWidget(widget)

        //THEN
        verify(widgetRepository, times(1)).save(widget)
        verifyZeroInteractions(memoryStorage)
        assertThat(result.id).isEqualTo(widget.id)
        assertThat(result.zIndex).isEqualTo(widget.zIndex)
    }

    @Test
    fun `should create Widget in memory`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        whenever(memoryStorage.save(widget)).thenReturn(widget)

        //WHEN
        val result = widgetAdapter.createWidget(widget)

        //THEN
        verify(memoryStorage, times(1)).save(widget)
        verifyZeroInteractions(widgetRepository)
        assertThat(result.id).isEqualTo(widget.id)
        assertThat(result.zIndex).isEqualTo(widget.zIndex)
    }

    @Test
    fun `should get Widget By Id in DB`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetRepository.getOne(widgetId)).thenReturn(widget)

        //WHEN
        val result = widgetAdapter.getWidgetById(widgetId)

        //THEN
        verify(widgetRepository, times(1)).getOne(widgetId)
        verifyZeroInteractions(memoryStorage)
        assertThat(result.id).isEqualTo(widget.id)
        assertThat(result.zIndex).isEqualTo(widget.zIndex)
    }

    @Test
    fun `should get Widget By Id in memory`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        whenever(memoryStorage.getWidgetById(widgetId)).thenReturn(widget)

        //WHEN
        val result = widgetAdapter.getWidgetById(widgetId)

        //THEN
        verify(memoryStorage, times(1)).getWidgetById(widgetId)
        verifyZeroInteractions(widgetRepository)
        assertThat(result.id).isEqualTo(widget.id)
        assertThat(result.zIndex).isEqualTo(widget.zIndex)
    }

    @Test
    fun `should update Widget in DB`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        whenever(widgetRepository.save(widget)).thenReturn(widget)

        //WHEN
        val result = widgetAdapter.updateWidget(widget)

        //THEN
        verify(widgetRepository, times(1)).save(widget)
        verifyZeroInteractions(memoryStorage)
        assertThat(result.id).isEqualTo(widget.id)
        assertThat(result.zIndex).isEqualTo(widget.zIndex)
    }

    @Test
    fun `should update Widget in memory`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        whenever(memoryStorage.updateWidget(widget)).thenReturn(widget)

        //WHEN
        val result = widgetAdapter.updateWidget(widget)

        //THEN
        verify(memoryStorage, times(1)).updateWidget(widget)
        verifyZeroInteractions(widgetRepository)
        assertThat(result.id).isEqualTo(widget.id)
        assertThat(result.zIndex).isEqualTo(widget.zIndex)
    }

    @Test
    fun `should get all Widgets filtered paged and sorted in DB`() {
        //GIVEN
        val filter = FilterWidgetsRequest(0, 0, 400, 400)
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "zIndex")
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        whenever(widgetRepository.findAllPagedFilteredSorted(
                filter.lowerLeftX!!, filter.lowerLeftY!!, filter.upperRightX!!, filter.upperRightY!!, pageable))
                .thenReturn(PageImpl<Widget>(widgets, pageable, widgets.size.toLong()))

        //WHEN
        val result = widgetAdapter.getAllWidgetsFilteredPagedSorted(pageable, filter)

        //THEN
        verify(widgetRepository, times(1)).findAllPagedFilteredSorted(any(), any(), any(), any(), any())
        assertThat(result.totalElements).isEqualTo(11)
        assertThat(result.pageable.isPaged).isTrue()
        assertThat(result.totalPages).isEqualTo(2)
        assertThat(result.pageable.pageSize).isEqualTo(10)
    }

    @Test
    fun `should get all Widgets not filtered paged and sorted in DB`() {
        //GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "zIndex")
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        whenever(widgetRepository.findAllPagedSorted(pageable))
                .thenReturn(PageImpl<Widget>(widgets, pageable, widgets.size.toLong()))

        //WHEN
        val result = widgetAdapter.getAllWidgetsFilteredPagedSorted(pageable, null)

        //THEN
        verify(widgetRepository, times(1)).findAllPagedSorted(pageable)
        assertThat(result.totalElements).isEqualTo(11)
        assertThat(result.pageable.isPaged).isTrue()
        assertThat(result.totalPages).isEqualTo(2)
        assertThat(result.pageable.pageSize).isEqualTo(10)
    }

    @Test
    fun `should get all Widgets filtered paged and sorted in memory`() {
        //GIVEN
        val filter = FilterWidgetsRequest(0, 0, 400, 400)
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "zIndex")
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        whenever(memoryStorage.getAllPagedFilteredSorted(
                pageable, filter.lowerLeftX!!, filter.lowerLeftY!!, filter.upperRightX!!, filter.upperRightY!!))
                .thenReturn(PageImpl<Widget>(widgets, pageable, widgets.size.toLong()))

        //WHEN
        val result = widgetAdapter.getAllWidgetsFilteredPagedSorted(pageable, filter)

        //THEN
        verify(memoryStorage, times(1)).getAllPagedFilteredSorted(any(), any(), any(), any(), any())
        verifyZeroInteractions(widgetRepository)
        assertThat(result.totalElements).isEqualTo(11)
        assertThat(result.totalElements).isEqualTo(11)
        assertThat(result.pageable.isPaged).isTrue()
        assertThat(result.totalPages).isEqualTo(2)
        assertThat(result.pageable.pageSize).isEqualTo(10)
    }

    @Test
    fun `should get all Widgets not filtered paged and sorted in memory`() {
        //GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "zIndex")
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        whenever(memoryStorage.getAllPagedSorted(pageable))
                .thenReturn(PageImpl<Widget>(widgets, pageable, widgets.size.toLong()))

        //WHEN
        val result = widgetAdapter.getAllWidgetsFilteredPagedSorted(pageable, null)

        //THEN
        verify(memoryStorage, times(1)).getAllPagedSorted(pageable)
        verifyZeroInteractions(widgetRepository)
        assertThat(result.totalElements).isEqualTo(11)
        assertThat(result.totalElements).isEqualTo(11)
        assertThat(result.pageable.isPaged).isTrue()
        assertThat(result.totalPages).isEqualTo(2)
        assertThat(result.pageable.pageSize).isEqualTo(10)
    }

    @Test
    fun `should check zIndex already exists in DB`() {
        //GIVEN
        val zIndex = 0

        whenever(widgetRepository.findByZIndex(zIndex)).thenReturn(null)

        //WHEN
        val result = widgetAdapter.checkZIndexAlreadyExists(zIndex)

        //THEN
        verify(widgetRepository, times(1)).findByZIndex(zIndex)
        verifyZeroInteractions(memoryStorage)
        assertThat(result).isFalse()
    }

    @Test
    fun `should check zIndex already exists in memory`() {
        //GIVEN
        val zIndex = 0

        whenever(memoryStorage.checkZIndexAlreadyExists(zIndex)).thenReturn(false)
        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)

        //WHEN
        val result = widgetAdapter.checkZIndexAlreadyExists(zIndex)

        //THEN
        verify(memoryStorage, times(1)).checkZIndexAlreadyExists(zIndex)
        verifyZeroInteractions(widgetRepository)
        assertThat(result).isFalse()
    }

    @Test
    fun `should increment higher and equal zIndexes in DB`() {
        //GIVEN
        val zIndex = 1
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        whenever(widgetRepository.findByZIndexGreaterThanEqual(zIndex, Sort.by(Sort.Direction.ASC, "zIndex")))
                .thenReturn(widgets)
        for (w in widgets) {
            whenever(widgetRepository.save(w)).thenReturn(w)
        }

        //WHEN
        widgetAdapter.incrementHigherAndEqualZIndexes(zIndex)

        //THEN
        verifyZeroInteractions(memoryStorage)
    }

    @Test
    fun `should increment higher and equal zIndexes in memory`() {
        //GIVEN
        val zIndex = 1
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        val widgets = mutableListOf<Widget>()

        for (i in widgetIds.indices) {
            widgets.add(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        doNothing().whenever(memoryStorage).incrementHigherAndEqualZIndexes(zIndex)

        //WHEN
        widgetAdapter.incrementHigherAndEqualZIndexes(zIndex)

        //THEN
        verify(memoryStorage, times(1)).incrementHigherAndEqualZIndexes(zIndex)
        verifyZeroInteractions(widgetRepository)
    }

    @Test
    fun `should get lowest zIndex in DB`() {
        //GIVEN
        val zIndex = -999

        whenever(widgetRepository.findLowestZIndex()).thenReturn(zIndex)

        //WHEN
        val result = widgetAdapter.getLowestZIndex()

        //THEN
        verify(widgetRepository, times(1)).findLowestZIndex()
        verifyZeroInteractions(memoryStorage)
        assertThat(result).isEqualTo(zIndex)
    }

    @Test
    fun `should get lowest zIndex in memory`() {
        //GIVEN
        val zIndex = -999

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        whenever(memoryStorage.findLowestZIndex()).thenReturn(zIndex)

        //WHEN
        val result = widgetAdapter.getLowestZIndex()

        //THEN
        verify(memoryStorage, times(1)).findLowestZIndex()
        verifyZeroInteractions(widgetRepository)
        assertThat(result).isEqualTo(zIndex)
    }

    @Test
    fun `should delete Widget from DB`() {
        //GIVEN
        val widgetId = 1L

        doNothing().whenever(widgetRepository).deleteById(widgetId)

        //WHEN
        widgetAdapter.deleteWidget(widgetId)

        //THEN
        verify(widgetRepository, times(1)).deleteById(widgetId)
        verifyZeroInteractions(memoryStorage)
    }

    @Test
    fun `should delete Widget from memory`() {
        //GIVEN
        val widgetId = 1L

        ReflectionTestUtils.setField(widgetAdapter, "databaseActive", false)
        doNothing().whenever(memoryStorage).deleteWidget(widgetId)

        //WHEN
        widgetAdapter.deleteWidget(widgetId)

        //THEN
        verify(memoryStorage, times(1)).deleteWidget(widgetId)
        verifyZeroInteractions(widgetRepository)
    }
}