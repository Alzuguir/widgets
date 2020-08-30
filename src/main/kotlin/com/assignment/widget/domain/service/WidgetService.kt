package com.assignment.widget.domain.service

import com.assignment.widget.adapter.WidgetAdapter
import com.assignment.widget.boundary.dto.CreateWidgetRequest
import com.assignment.widget.boundary.dto.FilterWidgetsRequest
import com.assignment.widget.boundary.dto.UpdateWidgetRequest
import com.assignment.widget.domain.domainobject.Widget
import com.assignment.widget.domain.mapper.WidgetMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WidgetService(private val widgetAdapter: WidgetAdapter) {

    fun getWidgetById(id: Long): Widget =
            widgetAdapter.getWidgetById(id)

    @Transactional
    fun createWidget(request: CreateWidgetRequest): Widget =
            when {
                request.zIndex == null ->
                    widgetAdapter.createWidget(WidgetMapper.mapCreateRequestToWidget(request, widgetAdapter.getLowestZIndex() - 1))

                widgetAdapter.checkZIndexAlreadyExists(request.zIndex) -> {
                    widgetAdapter.incrementHigherAndEqualZIndexes(request.zIndex)
                            .let { widgetAdapter.createWidget(WidgetMapper.mapCreateRequestToWidget(request)) }
                }

                else ->
                    widgetAdapter.createWidget(WidgetMapper.mapCreateRequestToWidget(request))
            }

    @Transactional
    fun updateWidget(request: UpdateWidgetRequest, id: Long): Widget =
            widgetAdapter.getWidgetById(id)
                    .let { widget ->
                        if (request.zIndex != null && widgetAdapter.checkZIndexAlreadyExists(request.zIndex)) {
                            widgetAdapter.incrementHigherAndEqualZIndexes(request.zIndex)
                            widgetAdapter.updateWidget(WidgetMapper.mapUpdateRequestToWidget(request, widget))
                        } else {
                            widgetAdapter.updateWidget(WidgetMapper.mapUpdateRequestToWidget(request, widget))
                        }
                    }

    fun deleteWidget(id: Long): Unit =
            widgetAdapter.deleteWidget(id)

    fun getAllWidgets(pageable: Pageable, filter: FilterWidgetsRequest?): Page<Widget> =
            filterWidgets(filter)
                    .let { widgets ->
                        PageImpl<Widget>(widgets, pageable, widgets.size.toLong())
                    }

    private fun filterWidgets(filter: FilterWidgetsRequest?): List<Widget> =
            if (filter?.lowerLeftX != null && filter.lowerLeftY != null && filter.upperRightX != null && filter.upperRightY != null)
                widgetAdapter.getAllWidgetsSorted().filter { widget ->
                    (widget.x - (widget.width / 2) >= filter.lowerLeftX && widget.y - (widget.height / 2) >= filter.lowerLeftY)
                            && (widget.x + (widget.width / 2) <= filter.upperRightX && widget.y + (widget.height / 2) <= filter.upperRightY)
                }
            else
                widgetAdapter.getAllWidgetsSorted()

}