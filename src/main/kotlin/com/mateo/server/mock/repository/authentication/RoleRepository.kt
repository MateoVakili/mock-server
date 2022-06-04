package com.mateo.server.mock.repository.authentication

import com.mateo.server.mock.entity.authentication.Role
import com.mateo.server.mock.entity.authentication.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: RoleType): Role?
}