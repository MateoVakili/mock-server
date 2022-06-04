package com.mateo.server.mock.service

import com.mateo.server.mock.entity.Userentity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsImpl(
    val id: Long,
    val email: String,
    private val username: String,
    private val password: String,
    private val authorities: MutableList<SimpleGrantedAuthority>
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username


    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    companion object {
        fun build(userEntity: Userentity): UserDetailsImpl {
            val authorities = userEntity.roles
                .map { role -> SimpleGrantedAuthority(role.name.name) }

            return UserDetailsImpl(
                id = userEntity.id ?: 0,
                email = userEntity.email,
                username = userEntity.username,
                password = userEntity.password,
                authorities = authorities.toMutableList()
            )
        }
    }
}
