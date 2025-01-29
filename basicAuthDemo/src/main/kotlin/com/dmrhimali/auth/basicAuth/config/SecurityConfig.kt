package com.dmrhimali.auth.basicAuth.config


import com.dmrhimali.auth.basicAuth.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity(debug = true)
open class SecurityConfig(private val userRepository: UserRepository) {

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/login", "/api/register", "/admin/addAdmin", "/logout").permitAll()  // Allow unauthenticated access to /login and /register
                    .requestMatchers("/api/sayhello").authenticated()        // Require authentication for /sayhello
                    .requestMatchers("/admin/users").hasRole("ADMIN") // Require 'ROLE_ADMIN' to access /admin/users
                    .anyRequest().authenticated()                        // All other requests need authentication
            }
            .formLogin { formLogin ->
                formLogin
                    .loginProcessingUrl("/login")                         // Handle login POST request at /login
                    .defaultSuccessUrl("/api/sayhello", true)
                    .failureUrl("/login?error=true")  // Redirect to /login if authentication fails
                    .permitAll()                                         // Allow unauthenticated access to /login
            }
            .logout { logout ->
                logout
                    .permitAll()                                       // Allow unauthenticated access to logout
            }
            .csrf { csrf ->
                csrf.disable()                                      // Disable CSRF for simplicity (enable for production)
            }
            return http.build()
        }

    @Bean
    open fun authenticationManager(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {

        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)

        return ProviderManager(authenticationProvider)
    }


    @Bean
    open fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            val user = userRepository.findByUsername(username)
                .orElseThrow{UsernameNotFoundException("User not found")}

            User.builder()
                .username(user.username)
                .password(user.password)
                .roles(user.role)  // Role needs to be prefixed with "ROLE_" (e.g., "ROLE_USER")
                .build()
        }
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}


