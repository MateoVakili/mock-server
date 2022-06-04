package com.mateo.server.mock.repository

import com.mateo.server.mock.entity.RefreshToken
import com.mateo.server.mock.entity.Userentity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository


@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByToken(token: String?): RefreshToken?

    @Modifying
    fun deleteByUserEntity(userEntity: Userentity): Int
}