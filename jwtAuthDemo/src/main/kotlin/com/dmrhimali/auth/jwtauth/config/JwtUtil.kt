package com.dmrhimali.auth.jwtauth.config


import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtUtil {
     val jwtExpirationMs = 360000L  // 12 minute expiration for access token
     val jwtRefreshExpirationMs = 720000L  // 24 minute expiration for refresh token

    // Use a secure key for signing (use your actual secret key in production)
    private val secretKey: Key = Keys.secretKeyFor(SignatureAlgorithm.HS512)

    // Generate JWT token for the username and roles
    fun generateToken(username: String, roles: List<String>): String {
        val now = Date()

        // Add roles as a claim (roles should be a list of strings)
        return Jwts.builder()
            .setSubject(username)
            .claim("roles", roles)  // Include roles in the JWT payload
            .setIssuedAt(now)
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS512)  // Use the signing key
            .compact()
    }

    // Validate the JWT token
    fun validateToken(token: String, username: String): Boolean {
        val extractedUsername = extractUsername(token)
        return (extractedUsername == username && !isTokenExpired(token))
    }

    // Extract username from the JWT token
    fun extractUsername(token: String): String {
        return extractClaims(token).subject
    }

    // Extract roles from the JWT token
    fun extractRoles(token: String): List<String> {
        @Suppress("UNCHECKED_CAST")
        return extractClaims(token).get("roles", List::class.java) as List<String>
    }

    // Check if the token has expired
    fun isTokenExpired(token: String): Boolean {
        return extractClaims(token).expiration.before(Date())
    }

    private fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }

    // Resolve the token from the request header
    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)  // Extract token after "Bearer "
        } else null
    }

    // Get Authentication object from JWT
    fun getAuthentication(token: String): Authentication {
        val username = extractUsername(token)
        val roles = extractRoles(token).map { SimpleGrantedAuthority(it) }
        val userDetails = User(username, "", roles)

        return org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, token, userDetails.authorities)
    }

}