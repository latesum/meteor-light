package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.ActivateCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ActivateCodeDao : JpaRepository<ActivateCode?, Long> {

    // SQL type: const.
    @Query("FROM ActivateCode ac JOIN FETCH ac.user WHERE ac.code = ?1 AND ac.user.id = ?2")
    fun findWithUserByCodeAndUserId(code: String, userId: Long): ActivateCode?

    // SQL type: const.
    @Query("FROM ActivateCode ac JOIN ac.user u WHERE u.email = ?1")
    fun findWithUserByUserEmail(email: String): ActivateCode?

}
