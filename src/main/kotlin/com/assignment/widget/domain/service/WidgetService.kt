package com.assignment.widget.domain.service

import com.assignment.widget.adapter.WidgetAdapter
import com.assignment.widget.boundary.dto.CreateWidgetRequest
import com.assignment.widget.boundary.dto.UpdateWidgetRequest
import com.assignment.widget.domain.domainobject.Widget
import com.assignment.widget.domain.service.mapper.WidgetMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WidgetService(private val widgetAdapter: WidgetAdapter) {

    fun getWidgetById(id: Long): Widget =
            widgetAdapter.getWidgetById(id)

    fun createWidget(request: CreateWidgetRequest): Widget =
            when {
                request.zIndex == null ->
                    widgetAdapter.createWidget(WidgetMapper.mapCreateRequestNullZIndexToWidget(request, widgetAdapter.getLowestZIndex() - 1))

                widgetAdapter.checkZIndexAlreadyExists(request.zIndex) ->
                    bumpZIndexesAndCreateWidget(request, request.zIndex)

                else ->
                    widgetAdapter.createWidget(WidgetMapper.mapCreateRequestToWidget(request))
            }

    @Transactional
    fun updateWidget(request: UpdateWidgetRequest, id: Long): Widget =
            widgetAdapter.getWidgetById(id)
                    .let { widget ->
                        if (request.zIndex != null && widgetAdapter.checkZIndexAlreadyExists(request.zIndex)) {
                            widgetAdapter.bumpHigherAndEqualZIndexes(request.zIndex)
                            widgetAdapter.updateWidget(WidgetMapper.mapUpdateRequestToWidget(request, widget))
                        } else {
                            widgetAdapter.updateWidget(WidgetMapper.mapUpdateRequestToWidget(request, widget))
                        }
                    }

    fun deleteWidget(id: Long): Unit =
            widgetAdapter.deleteWidget(id)

    fun getAllWidgets(): List<Widget> =
            widgetAdapter.getAllWidgets().sortedBy { widget -> widget.zIndex }

    private fun bumpZIndexesAndCreateWidget(request: CreateWidgetRequest, zIndex: Int): Widget =
            widgetAdapter.bumpHigherAndEqualZIndexes(zIndex)
                    .let { widgetAdapter.createWidget(WidgetMapper.mapCreateRequestToWidget(request)) }

}