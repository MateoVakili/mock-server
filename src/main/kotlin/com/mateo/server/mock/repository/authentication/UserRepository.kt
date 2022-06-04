package com.mateo.server.mock.repository.authentication

import com.mateo.server.mock.entity.authentication.Userentity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository : JpaRepository<Userentity, Long> {
    fun findByUsername(username: String?): Userentity?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}
