package com.assignment.widget.boundary.dto

data class FilterWidgetsRequest(
        val lowerLeftX: Int,
        val lowerLeftY: Int,
        val upperRightX: Int,
        val upperRightY: Int
)