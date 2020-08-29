package com.assignment.widget.domain.service.mapper

import com.assignment.widget.boundary.dto.CreateWidgetRequest
import com.assignment.widget.boundary.dto.UpdateWidgetRequest
import com.assignment.widget.domain.domainobject.Widget

object WidgetMapper {

    fun mapCreateRequestToWidget(request: CreateWidgetRequest): Widget =
            Widget(
                    x = request.x,
                    y = request.y,
                    height = request.height,
                    width = request.width,
                    zIndex = request.zIndex!!
            )

    fun mapUpdateRequestToWidget(request: UpdateWidgetRequest, widget: Widget): Widget =
            widget.copy(
                    x = request.x ?: widget.x,
                    y = request.y ?: widget.y,
                    height = request.height ?: widget.height,
                    width = request.width ?: widget.width,
                    zIndex = request.zIndex ?: widget.zIndex
            )

    fun mapCreateRequestNullZIndexToWidget(request: CreateWidgetRequest, foregroundZIndex: Int): Widget =
            Widget(
                    x = request.x,
                    y = request.y,
                    height = request.height,
                    width = request.width,
                    zIndex = request.zIndex ?: foregroundZIndex
            )
}