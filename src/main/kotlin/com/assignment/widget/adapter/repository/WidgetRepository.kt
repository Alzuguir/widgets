package com.assignment.widget.adapter.repository

import com.assignment.widget.domain.domainobject.Widget
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WidgetRepository : CrudRepository<Widget, Long>