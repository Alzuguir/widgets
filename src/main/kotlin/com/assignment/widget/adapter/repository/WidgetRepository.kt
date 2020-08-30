package com.assignment.widget.adapter.repository

import com.assignment.widget.domain.domainobject.Widget
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    @Query("SELECT w FROM Widget w WHERE " +
            "(w.x - (w.width / 2) >= :flx AND w.y - (w.height / 2) >= :fly)" +
            "AND (w.x + (w.width / 2) <= :fux AND w.y + (w.height / 2) <= :fuy)")
    fun findAllPagedFilteredSorted(
            @Param("flx") filterLowerX: Int,
            @Param("fly") filterLowerY: Int,
            @Param("fux") filterUpperX: Int,
            @Param("fuy") filterUpperY: Int,
            pageable: Pageable
    ): Page<Widget>

    @Query("SELECT w FROM Widget w")
    fun findAllPagedSorted(pageable: Pageable): Page<Widget>
}