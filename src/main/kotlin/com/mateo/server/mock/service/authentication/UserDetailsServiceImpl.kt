package com.mateo.server.mock.service.authentication

import com.mateo.server.mock.repository.authentication.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl constructor(
    @Autowired val userRepository: UserRepository
) : UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        userRepository.findByUsername(username)?.let {
            return UserDetailsImpl.build(it)
        } ?: throw UsernameNotFoundException("user not found with username: $username")
    }
}
