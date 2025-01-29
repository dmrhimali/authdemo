package com.dmrhimali.auth.ssoauth.config



import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity(debug = true)
open class SecurityConfig {

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { requests ->
                // Publicly accessible endpoints
                requests
                    .requestMatchers(   "/", "/index", "/login", "/error", "/login/oauth2/code/google", "/static/**").permitAll()  // Allow access to login, error, and callback endpoints
                    .anyRequest().authenticated()  // Require authentication for all other endpoints
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .loginPage("/login")  // Custom login page (optional)
                    .defaultSuccessUrl("/dashboard", true)  // Redirect to dashboad after login
                    .failureUrl("/login?error=true")  // Redirect to /login if authentication fails
            }
            .logout { logout ->
                // Allow logging out from the application and redirect to Google logout URL
                logout
                    .logoutUrl("/logout")  // Custom logout URL
                    //.logoutSuccessUrl("https://accounts.google.com/Logout") // Redirect to Google logout page
                    .logoutSuccessUrl("/logout-success") // Redirect to this URL after logout
                    .permitAll()
            }.csrf { csrf ->
                // Disable CSRF protection for the logout URL to prevent 403 error
                csrf
                    .ignoringRequestMatchers("/logout")  // Ignore CSRF protection for /logout
            }

        return http.build()
    }
}
