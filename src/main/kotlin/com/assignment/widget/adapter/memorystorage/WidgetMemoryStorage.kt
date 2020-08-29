package com.assignment.widget.adapter.memorystorage

import com.assignment.widget.domain.domainobject.Widget
import javassist.NotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class WidgetMemoryStorage {

    private val memoryStorage: MutableMap<Long, Widget> = mutableMapOf()

    fun save(widgetToBeSaved: Widget): Widget =
            widgetToBeSaved.copy(id = assignWidgetId(), lastModification = Date())
                    .let { widget ->
                        memoryStorage[widget.id] = widget
                        widget
                    }

    fun getWidgetById(id: Long): Widget = memoryStorage[id] ?: throw NotFoundException("Widget $id was not found")

    private fun assignWidgetId(): Long =
            memoryStorage.values.maxBy { widget -> widget.id }?.id?.plus(1) ?: 1

    fun updateWidget(widgetUpdate: Widget): Widget =
            getWidgetById(widgetUpdate.id).let { widget ->
                memoryStorage.replace(widgetUpdate.id,
                        widget.copy(
                                x = widgetUpdate.x,
                                y = widgetUpdate.y,
                                zIndex = widgetUpdate.zIndex,
                                width = widgetUpdate.width,
                                height = widgetUpdate.height,
                                lastModification = widgetUpdate.lastModification
                        )
                )
                getWidgetById(widgetUpdate.id)
            }

    fun deleteWidget(id: Long) {
        val removed = memoryStorage.remove(id)
        if (removed == null) throw NotFoundException("Widget $id was not found")
    }

    fun getAllWidgets(): List<Widget> =
            memoryStorage.values.toList()

    fun checkZIndexAlreadyExists(zIndex: Int): Boolean =
            memoryStorage.values.find { widget -> widget.zIndex == zIndex } != null

    fun bumpHigherAndEqualZIndexes(zIndex: Int): Unit =
            memoryStorage.values.sortedBy { zIndex }
                    .forEach { widget ->
                        if (widget.zIndex >= zIndex)
                            memoryStorage.replace(widget.id, widget.copy(zIndex = widget.zIndex + 1))
                    }

    fun getLowestZIndex(): Int =
            memoryStorage.values.minBy { widget -> widget.zIndex }?.zIndex ?: 0
}