package com.dmrhimali.auth.jwtauth.config

import jakarta.servlet.ServletException
import java.io.IOException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource

class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authHeader = request.getHeader("Authorization")
        val requestURI = request.requestURI

        // Skip the /register and /login routes for JWT Authentication (they don't require tokens)
        if (requestURI.startsWith("/api/register") || requestURI.startsWith("/api/login")) {
            chain.doFilter(request, response)
            return
        }

        // Proceed with JWT token validation for authenticated endpoints
        val token = jwtUtil.resolveToken(request)
        if (token != null && jwtUtil.validateToken(token, jwtUtil.extractUsername(token))) {
            val authentication = jwtUtil.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain.doFilter(request, response)
    }
}
