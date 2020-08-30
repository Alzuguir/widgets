package com.assignment.widget.adapter.memorystorage

import com.assignment.widget.domain.domainobject.Widget
import com.assignment.widget.domain.exception.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class WidgetMemoryStorage {

    private val memoryStorage: MutableMap<Long, Widget> = mutableMapOf()
    private var id: Long = 0

    fun save(widgetToBeSaved: Widget): Widget =
            widgetToBeSaved.copy(id = id, lastModification = Date())
                    .let { widget ->
                        memoryStorage[widget.id] = widget
                        id++
                        widget
                    }

    fun getWidgetById(id: Long): Widget = memoryStorage[id]
            ?: throw NotFoundException("Widget $id was not found")

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
        memoryStorage.remove(id) ?: throw NotFoundException("Widget $id was not found")
    }

    fun getAllWidgetsSorted(): List<Widget> =
            memoryStorage.values.sortedBy { widget -> widget.zIndex }

    fun checkZIndexAlreadyExists(zIndex: Int): Boolean =
            memoryStorage.values.find { widget -> widget.zIndex == zIndex } != null

    fun incrementHigherAndEqualZIndexes(zIndex: Int): Unit =
            memoryStorage.values.sortedBy { zIndex }
                    .forEach { widget ->
                        if (widget.zIndex >= zIndex)
                            memoryStorage.replace(widget.id, widget.copy(zIndex = widget.zIndex + 1))
                    }

    fun findLowestZIndex(): Int =
            memoryStorage.values.minBy { widget -> widget.zIndex }?.zIndex ?: 0

    fun getAllPagedFilteredSorted(
            pageable: Pageable,
            lowerLeftX: Int,
            lowerLeftY: Int,
            upperRightX: Int,
            upperRightY: Int
    ): Page<Widget> =
            memoryStorage.values.filter { widget ->
                (widget.x - (widget.width / 2) >= lowerLeftX && widget.y - (widget.height / 2) >= lowerLeftY)
                        && (widget.x + (widget.width / 2) <= upperRightX && widget.y + (widget.height / 2) <= upperRightY)
            }.sortedBy { widget -> widget.zIndex }
                    .let { widgets -> PageImpl<Widget>(widgets, pageable, widgets.size.toLong()) }

    fun getAllPagedSorted(pageable: Pageable): Page<Widget> =
            PageImpl<Widget>(
                    memoryStorage.values.toList().sortedBy { widget -> widget.zIndex },
                    pageable,
                    memoryStorage.values.size.toLong()
            )

}