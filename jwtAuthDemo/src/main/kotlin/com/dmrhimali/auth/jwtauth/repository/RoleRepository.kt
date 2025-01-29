package com.dmrhimali.auth.jwtauth.repository

import com.dmrhimali.auth.jwtauth.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: String): Optional<Role>
    fun findByNameIn(names: List<String>): List<Role>
}