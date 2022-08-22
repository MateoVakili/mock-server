package com.mateo.server.mock.entity.authentication

import javax.persistence.*

@Entity(name = "password")
data class Password(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    @OneToOne
    @JoinColumn(name = "userentity_id", referencedColumnName = "id")
    var userEntity: Userentity? = null,
    @Column(nullable = false)
    var password: String? = null,
    @Column(nullable = false, unique = true)
    var accountUserName: String? = null,
    @Column(nullable = false)
    var url: String? = null,
)
