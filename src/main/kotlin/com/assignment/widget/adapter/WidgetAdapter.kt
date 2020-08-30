package com.assignment.widget.adapter

import com.assignment.widget.adapter.memorystorage.WidgetMemoryStorage
import com.assignment.widget.adapter.repository.WidgetRepository
import com.assignment.widget.domain.domainobject.Widget
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service


@Service
class WidgetAdapter(
        private val widgetMemoryStorage: WidgetMemoryStorage,
        private val widgetRepository: WidgetRepository
) {

    @Value("\${database.active}")
    private val databaseActive: Boolean = true

    fun createWidget(widget: Widget): Widget =
            if (databaseActive) widgetRepository.save(widget)
            else widgetMemoryStorage.save(widget)

    fun getWidgetById(id: Long): Widget =
            if (databaseActive) widgetRepository.getOne(id)
            else widgetMemoryStorage.getWidgetById(id)

    fun updateWidget(widgetToUpdate: Widget) =
            if (databaseActive) widgetRepository.save(widgetToUpdate)
            else widgetMemoryStorage.updateWidget(widgetToUpdate)

    fun deleteWidget(id: Long): Unit =
            if (databaseActive) widgetRepository.deleteById(id)
            else widgetMemoryStorage.deleteWidget(id)

    fun getAllWidgetsSorted(): List<Widget> =
            if (databaseActive) widgetRepository.findAll(Sort.by("zIndex"))
            else widgetMemoryStorage.getAllWidgetsSorted()

    fun checkZIndexAlreadyExists(zIndex: Int): Boolean =
            if (databaseActive) widgetRepository.findByZIndex(zIndex) != null
            else widgetMemoryStorage.checkZIndexAlreadyExists(zIndex)

    fun incrementHigherAndEqualZIndexes(zIndex: Int) =
            if (databaseActive) incrementHigherAndEqualZIndexesH2(zIndex)
            else widgetMemoryStorage.incrementHigherAndEqualZIndexes(zIndex)

    fun getLowestZIndex(): Int =
            if (databaseActive) widgetRepository.findLowestZIndex()
            else widgetMemoryStorage.findLowestZIndex()

    private fun incrementHigherAndEqualZIndexesH2(zIndex: Int) {
        widgetRepository.findByZIndexGreaterThanEqual(zIndex, Sort.by("zIndex"))
                .forEach { widget ->
                    if (widget.zIndex >= zIndex)
                        widgetRepository.save(widget.copy(zIndex = widget.zIndex + 1))
                }
    }
}