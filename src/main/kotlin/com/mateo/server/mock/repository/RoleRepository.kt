package com.mateo.server.mock.repository

import com.mateo.server.mock.entity.Role
import com.mateo.server.mock.entity.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: RoleType): Role?
}