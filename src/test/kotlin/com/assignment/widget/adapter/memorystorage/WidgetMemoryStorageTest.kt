package com.assignment.widget.adapter.memorystorage

import com.assignment.widget.WidgetTestFixtures
import com.assignment.widget.domain.exception.NotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

internal class WidgetMemoryStorageTest {

    private val memoryStorage = WidgetMemoryStorage()

    @Test
    fun `Should save widget in memory`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        //WHEN
        memoryStorage.save(widget)

        //THEN
        assertThat(memoryStorage.memory[widgetId]?.id).isEqualTo(widget.id)
        assertThat(memoryStorage.memory[widgetId]?.x).isEqualTo(widget.x)
        assertThat(memoryStorage.memory[widgetId]?.y).isEqualTo(widget.y)
        assertThat(memoryStorage.memory[widgetId]?.width).isEqualTo(widget.width)
        assertThat(memoryStorage.memory[widgetId]?.height).isEqualTo(widget.height)
        assertThat(memoryStorage.memory[widgetId]?.zIndex).isEqualTo(widget.zIndex)
    }

    @Test
    fun `Should get widget by id`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        //WHEN
        memoryStorage.save(widget)
        val result = memoryStorage.getWidgetById(widgetId)

        //THEN
        assertThat(result.id).isEqualTo(memoryStorage.memory[widgetId]?.id)
        assertThat(result.x).isEqualTo(memoryStorage.memory[widgetId]?.x)
        assertThat(result.y).isEqualTo(memoryStorage.memory[widgetId]?.y)
        assertThat(result.width).isEqualTo(memoryStorage.memory[widgetId]?.width)
        assertThat(result.height).isEqualTo(memoryStorage.memory[widgetId]?.height)
        assertThat(result.zIndex).isEqualTo(memoryStorage.memory[widgetId]?.zIndex)
    }

    @Test
    fun `Should throw NotFound exception while getting widget by id`() {
        //GIVEN
        val widgetId = 1L

        //THEN
        assertThatExceptionOfType(NotFoundException::class.java).isThrownBy {
            //WHEN
            memoryStorage.getWidgetById(widgetId)
        }.withMessage("Widget $widgetId was not found")
    }

    @Test
    fun `Should update widget`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val updatedZIndex = 3
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        //WHEN
        memoryStorage.save(widget)
        val result = memoryStorage.updateWidget(widget.copy(zIndex = updatedZIndex))

        //THEN
        assertThat(memoryStorage.memory[widgetId]?.id).isEqualTo(result.id)
        assertThat(memoryStorage.memory[widgetId]?.x).isEqualTo(result.x)
        assertThat(memoryStorage.memory[widgetId]?.y).isEqualTo(result.y)
        assertThat(memoryStorage.memory[widgetId]?.width).isEqualTo(result.width)
        assertThat(memoryStorage.memory[widgetId]?.height).isEqualTo(result.height)
        assertThat(memoryStorage.memory[widgetId]?.zIndex).isEqualTo(updatedZIndex)
    }

    @Test
    fun `Should delete Widget`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        //WHEN
        memoryStorage.save(widget)
        assertThat(memoryStorage.memory.size).isEqualTo(1)
        memoryStorage.deleteWidget(widgetId)

        //THEN
        assertThat(memoryStorage.memory).isEmpty()
    }

    @Test
    fun `Should get all widgets sorted`() {
        // GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        for (i in widgetIds.indices) {
            memoryStorage.save(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        val result = memoryStorage.getAllWidgetsSorted()

        //THEN
        assertThat(result.size).isEqualTo(memoryStorage.memory.size)
        assertThat(result[10].zIndex).isGreaterThan(result[9].zIndex)
    }

    @Test
    fun `check zIndex already exists`() {
        //GIVEN
        val widgetId = 1L
        val zIndex = 0
        val widget = WidgetTestFixtures.createWidget(widgetId, zIndex)

        memoryStorage.save(widget)

        //WHEN
        val result = memoryStorage.checkZIndexAlreadyExists(0)
        val result2 = memoryStorage.checkZIndexAlreadyExists(1)

        //THEN
        assertThat(result).isTrue()
        assertThat(result2).isFalse()
    }

    @Test
    fun `Should increment higher and equal zIndexes`() {
        // GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        for (i in widgetIds.indices) {
            memoryStorage.save(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        memoryStorage.incrementHigherAndEqualZIndexes(3)

        //THEN
        assertThat(memoryStorage.memory.values.maxBy { widget -> widget.zIndex }?.zIndex).isEqualTo(12)
    }

    @Test
    fun `Should find lowest zIndex`() {
        // GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -11)

        for (i in widgetIds.indices) {
            memoryStorage.save(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        val result = memoryStorage.findLowestZIndex()

        //THEN
        assertThat(result).isEqualTo(-11)
    }

    @Test
    fun `Should get all paged filtered sorted`() {
        // GIVEN
        val widgetInAreaIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
        val widgetOutAreaIds = listOf(12L, 13L, 14L, 15L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)

        for (i in widgetInAreaIds.indices) {
            memoryStorage.save(WidgetTestFixtures.createWidget(
                    id = widgetInAreaIds[i],
                    zIndex = zIndexes[i],
                    x = 50,
                    y = 50,
                    height = 20,
                    width = 20
            ))
        }

        for (i in widgetOutAreaIds.indices) {
            memoryStorage.save(WidgetTestFixtures.createWidget(
                    id = widgetOutAreaIds[i],
                    zIndex = zIndexes[i],
                    x = 50,
                    y = 50,
                    height = 300,
                    width = 300
            ))
        }

        // WHEN
        val result = memoryStorage.getAllPagedFilteredSorted(
                PageRequest.of(0, 10, Sort.Direction.ASC, "zIndex"),
                0,
                0,
                200,
                200
        )

        //THEN
        assertThat(result.numberOfElements).isEqualTo(7)
        assertThat(result.sort.isSorted).isTrue()
        assertThat(result.pageable.pageSize).isEqualTo(10)
        assertThat(result.totalPages).isEqualTo(1)
    }

    @Test
    fun `Should get all paged sorted`() {
        // GIVEN
        val widgetIds = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L)
        val zIndexes = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -11)

        for (i in widgetIds.indices) {
            memoryStorage.save(WidgetTestFixtures.createWidget(widgetIds[i], zIndexes[i]))
        }

        val result = memoryStorage.getAllPagedSorted(PageRequest.of(0, 10, Sort.Direction.ASC, "zIndex"))

        //THEN
        assertThat(result.pageable.pageSize).isEqualTo(10)
        assertThat(result.content.size).isEqualTo(11)
        assertThat(result.sort.isSorted).isTrue()
        assertThat(result.content[0].zIndex).isLessThan(result.content[1].zIndex)
    }
}