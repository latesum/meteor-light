package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserDao : JpaRepository<User?, String> {

    // SQL type: const.
    fun findByEmail(email: String): User?

    fun findByNickname(nickname: String): User?

}
