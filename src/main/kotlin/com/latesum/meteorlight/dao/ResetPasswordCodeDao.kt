package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.ResetPasswordCode
import org.springframework.data.jpa.repository.JpaRepository

interface ResetPasswordCodeDao : JpaRepository<ResetPasswordCode?, Long> {

    // SQL type: ref.
    fun findFirstByUserIdAndCodeOrderByIdDesc(userId: Long, code: String): ResetPasswordCode?

    // SQL type: ref.
    fun findFirstByUserIdOrderByIdDesc(userId: Long): ResetPasswordCode?

}
