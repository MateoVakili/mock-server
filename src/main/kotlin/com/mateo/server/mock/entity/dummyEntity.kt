package com.mateo.server.mock.entity

import javax.persistence.*

@Entity
@Table
data class DummyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val name: String
)
