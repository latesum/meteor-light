package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.ResetPasswordCode
import org.springframework.data.jpa.repository.JpaRepository

interface ResetPasswordCodeDao : JpaRepository<ResetPasswordCode?, String> {

    // SQL type: ref.
    fun findFirstByUserIdAndCodeOrderByIdDesc(userId: String, code: String): ResetPasswordCode?

    // SQL type: ref.
    fun findFirstByUserIdOrderByIdDesc(userId: String): ResetPasswordCode?

}
