package com.mateo.server.mock.entity

import javax.persistence.*

@Entity
@Table
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var name: RoleType
)