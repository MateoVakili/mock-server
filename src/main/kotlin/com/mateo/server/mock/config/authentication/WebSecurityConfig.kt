package com.mateo.server.mock.config.authentication

import com.mateo.server.mock.service.authentication.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig @Autowired constructor(
    private val userDetailsService: UserDetailsServiceImpl,
    private val unauthorizedHandler: AuthenticationEntryPointJwt,
    private val authenticationJwtTokenFilter: AuthenticationTokenFilter,
    private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests().antMatchers(
                "/authentication/login",
                "/authentication/register",
                "/authentication/refresh-token")
            .permitAll()
            .anyRequest().authenticated()
        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}
