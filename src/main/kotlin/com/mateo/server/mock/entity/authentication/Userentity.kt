package com.mateo.server.mock.entity.authentication

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["username", "email"]),
    ]
)
data class Userentity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @NotBlank
    var username: String,

    @NotBlank
    @Email
    var email: String,

    @NotBlank
    @Size(max = 120)
    var password: String,

    @NotNull
    var role: String
)

