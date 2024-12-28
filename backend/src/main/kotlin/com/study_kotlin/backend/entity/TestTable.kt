package com.study_kotlin.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "test_table")
class TestTable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int,

    @Column(length = 50)
    val value: String?
)

