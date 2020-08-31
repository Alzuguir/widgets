package com.assignment.widget.boundary.controller

import com.assignment.widget.boundary.dto.CreateWidgetRequest
import com.assignment.widget.boundary.dto.FilterWidgetsRequest
import com.assignment.widget.boundary.dto.UpdateWidgetRequest
import com.assignment.widget.domain.domainobject.Widget
import com.assignment.widget.domain.exception.InvalidPageSizeException
import com.assignment.widget.domain.service.WidgetService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/widgets")
class WidgetController(private val widgetService: WidgetService) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Widget =
            widgetService.getWidgetById(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: CreateWidgetRequest): Widget =
            widgetService.createWidget(request)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun update(@RequestBody request: UpdateWidgetRequest, @PathVariable id: Long): Widget =
            widgetService.updateWidget(request, id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long): Unit =
            widgetService.deleteWidget(id)

    @GetMapping
    fun getAll(@PageableDefault(page = 0, size = 10, sort = ["zIndex"]) pageable: Pageable, filter: FilterWidgetsRequest?): Page<Widget> =
            widgetService.getAllWidgets(pageable, filter)
                    .also { if (pageable.pageSize > 500) throw InvalidPageSizeException("Page size should not exceed 500") }
}