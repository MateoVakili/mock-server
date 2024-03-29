package com.mateo.server.mock.repository.authentication

import com.mateo.server.mock.entity.authentication.RefreshToken
import com.mateo.server.mock.entity.authentication.Userentity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Repository


@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByToken(token: String?): RefreshToken?

    @Modifying
    fun deleteAllByUserEntity(userEntity: Userentity): Int
}