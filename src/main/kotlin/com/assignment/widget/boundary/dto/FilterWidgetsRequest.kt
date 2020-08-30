package com.assignment.widget.boundary.dto

data class FilterWidgetsRequest(
        val lowerLeftX: Int? = null,
        val lowerLeftY: Int? = null,
        val upperRightX: Int? = null,
        val upperRightY: Int? = null
)