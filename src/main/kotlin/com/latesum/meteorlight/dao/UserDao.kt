package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserDao : JpaRepository<User?, Long> {

    // SQL type: const.
    fun findByEmail(email: String): User?

}
