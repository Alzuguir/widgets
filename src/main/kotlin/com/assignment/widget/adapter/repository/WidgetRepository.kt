package com.assignment.widget.adapter.repository

import com.assignment.widget.domain.domainobject.Widget
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WidgetRepository : JpaRepository<Widget, Long> {

    @Query("SELECT w FROM Widget w WHERE w.zIndex = :zIndex")
    fun findByZIndex(@Param("zIndex") zIndex: Int): Widget?

    @Query(value = "SELECT min(zIndex) FROM Widget")
    fun findLowestZIndex(): Int

    @Query("SELECT w FROM Widget w WHERE w.zIndex >= :zIndex")
    fun findByZIndexGreaterThanEqual(zIndex: Int, sort: Sort): List<Widget>
}