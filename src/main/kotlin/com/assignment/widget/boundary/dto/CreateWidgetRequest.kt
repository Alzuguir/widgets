package com.assignment.widget.boundary.dto

data class CreateWidgetRequest(
        val x: Int,
        val y: Int,
        val zIndex: Int?,
        val width: Int,
        val height: Int
)