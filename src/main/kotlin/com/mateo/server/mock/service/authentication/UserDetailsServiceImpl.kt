package com.mateo.server.mock.service.authentication

import com.mateo.server.mock.config.error.MockServerExceptions
import com.mateo.server.mock.entity.authentication.Userentity
import com.mateo.server.mock.model.authentication.UserDetailsImpl
import com.mateo.server.mock.repository.authentication.RefreshTokenRepository
import com.mateo.server.mock.repository.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl constructor(
    @Autowired val userRepository: UserRepository,
    @Autowired private val refreshTokenRepository: RefreshTokenRepository,
) : UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        userRepository.findByUsername(username)?.let {
            return UserDetailsImpl.build(it)
        } ?: throw UsernameNotFoundException("user not found with username: $username")
    }

    @Transactional
    fun removeUser(userName: String) {
        userRepository.findByUsername(userName)?.let { userEntity ->
            refreshTokenRepository.deleteByUserEntity(userEntity)
            userEntity.id?.let { userRepository.deleteById(it) }
        }
    }

    fun saveUser(user: Userentity) {
        validateEmail(user.email)
        validateUserName(user.username)
        userRepository.save(user)
    }

    private fun validateEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw MockServerExceptions.EmailAlreadyTakenException(email)
        }
    }

    private fun validateUserName(userName: String) {
        if (userRepository.existsByUsername(userName)) {
            throw MockServerExceptions.UserNameAlreadyTakenException(userName)
        }
    }
}
