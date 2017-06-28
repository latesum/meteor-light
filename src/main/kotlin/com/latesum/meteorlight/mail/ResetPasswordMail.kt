package com.latesum.meteorlight.mail

import com.google.common.io.Files
import java.io.File
import java.nio.charset.StandardCharsets

object ResetPasswordMail {

    val title = "Reset Password / 重置密码"
    val content: String

    init {
        val file = File(javaClass.classLoader.getResource("template/reset_password_email.html").file)
        content = Files.toString(file, StandardCharsets.UTF_8)
    }

}
