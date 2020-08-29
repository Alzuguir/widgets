package com.assignment.widget.adapter

import com.assignment.widget.adapter.memorystorage.WidgetMemoryStorage
import com.assignment.widget.domain.domainobject.Widget
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class WidgetAdapter(private val widgetMemoryStorage: WidgetMemoryStorage) {

    @Value("\${database.active}")
    private val databaseActive: Boolean? = null

    fun createWidget(widget: Widget): Widget =
            widgetMemoryStorage.save(widget)

    fun getWidgetById(id: Long): Widget =
            widgetMemoryStorage.getWidgetById(id)

    fun updateWidget(widgetToUpdate: Widget) =
            widgetMemoryStorage.updateWidget(widgetToUpdate)

    fun deleteWidget(id: Long): Unit =
            widgetMemoryStorage.deleteWidget(id)

    fun getAllWidgets(pageable: Pageable): Page<Widget> =
            widgetMemoryStorage.getAllWidgets(pageable)

    fun checkZIndexAlreadyExists(zIndex: Int): Boolean =
            widgetMemoryStorage.checkZIndexAlreadyExists(zIndex)

    fun bumpHigherAndEqualZIndexes(zIndex: Int) =
            widgetMemoryStorage.bumpHigherAndEqualZIndexes(zIndex)

    fun getLowestZIndex(): Int =
            widgetMemoryStorage.getLowestZIndex()
}