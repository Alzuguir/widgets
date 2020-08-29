package com.assignment.widget.domain.domainobject

import java.util.*
import javax.persistence.*

@Entity
@Table(
        name = "widget",
        uniqueConstraints = [
            UniqueConstraint(name = "uc_name", columnNames = ["id"]),
            UniqueConstraint(name = "uc_z_index", columnNames = ["z_index"])
        ]
)
data class Widget(
        @Id
        @GeneratedValue
        @Column
        val id: Long = -1L,
        @Column
        val x: Int,
        @Column
        val y: Int,
        @Column(name = "z_index")
        val zIndex: Int,
        @Column
        val width: Int,
        @Column
        val height: Int,
        @Column(name = "last_modification")
        val lastModification: Date? = null
)