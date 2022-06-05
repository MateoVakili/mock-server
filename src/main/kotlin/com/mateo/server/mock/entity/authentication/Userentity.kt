package com.mateo.server.mock.entity.authentication

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinTable(
        name = "userentity_roles",
        joinColumns = [JoinColumn(name = "userentity_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: Set<Role> = HashSet()
)

