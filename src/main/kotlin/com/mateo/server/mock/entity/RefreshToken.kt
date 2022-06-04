package com.mateo.server.mock.entity

import java.time.Instant
import javax.persistence.*

@Entity(name = "refreshtoken")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,
    @OneToOne
    @JoinColumn(name = "userentity_id", referencedColumnName = "id")
    var userEntity: Userentity? = null,
    @Column(nullable = false, unique = true)
    var token: String? = null,
    @Column(nullable = false)
    var expiryDate: Instant? = null
)