package com.assignment.widget.boundary.controller

import com.assignment.widget.boundary.dto.CreateWidgetRequest
import com.assignment.widget.boundary.dto.UpdateWidgetRequest
import com.assignment.widget.domain.domainobject.Widget
import com.assignment.widget.domain.service.WidgetService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetService: WidgetService) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Widget =
            widgetService.getWidgetById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun create(@RequestBody request: CreateWidgetRequest): Widget =
            widgetService.createWidget(request)

    @PutMapping("/{id}")
    fun update(@RequestBody request: UpdateWidgetRequest, @PathVariable id: Long): Widget =
            widgetService.updateWidget(request, id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long): Unit =
            widgetService.deleteWidget(id)

    @GetMapping
    fun getAll(): List<Widget> =
            widgetService.getAllWidgets()

}