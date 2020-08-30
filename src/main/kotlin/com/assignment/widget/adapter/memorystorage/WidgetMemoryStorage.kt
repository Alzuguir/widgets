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

    val memory: MutableMap<Long, Widget> = mutableMapOf()
    private var id: Long = 1

    fun save(widgetToBeSaved: Widget): Widget =
            widgetToBeSaved.copy(id = id, lastModification = Date())
                    .let { widget ->
                        memory[widget.id] = widget
                        id++
                        widget
                    }

    fun getWidgetById(id: Long): Widget = memory[id]
            ?: throw NotFoundException("Widget $id was not found")

    fun updateWidget(widgetUpdate: Widget): Widget =
            getWidgetById(widgetUpdate.id).let { widget ->
                memory.replace(widgetUpdate.id,
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
        memory.remove(id) ?: throw NotFoundException("Widget $id was not found")
    }

    fun getAllWidgetsSorted(): List<Widget> =
            memory.values.sortedBy { widget -> widget.zIndex }

    fun checkZIndexAlreadyExists(zIndex: Int): Boolean =
            memory.values.find { widget -> widget.zIndex == zIndex } != null

    fun incrementHigherAndEqualZIndexes(zIndex: Int): Unit =
            memory.values.sortedBy { zIndex }
                    .forEach { widget ->
                        if (widget.zIndex >= zIndex)
                            memory.replace(widget.id, widget.copy(zIndex = widget.zIndex + 1))
                    }

    fun findLowestZIndex(): Int =
            memory.values.minBy { widget -> widget.zIndex }?.zIndex ?: 0

    fun getAllPagedFilteredSorted(
            pageable: Pageable,
            lowerLeftX: Int,
            lowerLeftY: Int,
            upperRightX: Int,
            upperRightY: Int
    ): Page<Widget> =
            memory.values.filter { widget ->
                (widget.x - (widget.width / 2) >= lowerLeftX && widget.y - (widget.height / 2) >= lowerLeftY)
                        && (widget.x + (widget.width / 2) <= upperRightX && widget.y + (widget.height / 2) <= upperRightY)
            }.let { widgets ->
                PageImpl<Widget>(widgets, pageable, widgets.size.toLong())
            }

    fun getAllPagedSorted(pageable: Pageable): Page<Widget> =
            PageImpl<Widget>(
                    memory.values.toList().sortedBy { widget -> widget.zIndex },
                    pageable,
                    memory.values.size.toLong()
            )

}