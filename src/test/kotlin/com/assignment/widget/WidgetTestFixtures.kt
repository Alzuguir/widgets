package com.assignment.widget

import com.assignment.widget.boundary.dto.CreateWidgetRequest
import com.assignment.widget.boundary.dto.UpdateWidgetRequest
import com.assignment.widget.domain.domainobject.Widget

object WidgetTestFixtures {

    fun createWidget(
            id: Long,
            zIndex: Int,
            height: Int? = null,
            width: Int? = null,
            x: Int? = null,
            y: Int? = null
    ): Widget =
            Widget(
                    id = id,
                    x = x ?: 10,
                    y = y ?: 10,
                    zIndex = zIndex,
                    height = height ?: 100,
                    width = width ?: 200
            )

    fun createWidgetRequest(widget: Widget): CreateWidgetRequest =
            CreateWidgetRequest(
                    x = widget.x,
                    y = widget.y,
                    width = widget.width,
                    height = widget.height,
                    zIndex = widget.zIndex
            )

    fun updateWidgetRequest(widget: Widget): UpdateWidgetRequest =
            UpdateWidgetRequest(
                    x = widget.x,
                    y = widget.y,
                    width = widget.width,
                    height = widget.height,
                    zIndex = widget.zIndex
            )
}