package com.latesum.meteorlight.util

import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UserServiceUtil {

    // W3C email regex: https://www.w3.org/TR/html-markup/input.email.html#input.email.attrs.value.multiple .
    private val emailRegex = """^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$""".toRegex()

    private val passwordRegex = """^[A-Za-z0-9`\-=~!@#$%\^&*,._+]{8,20}$""".toRegex()

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(8))
    }

    fun checkPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

    fun isExpiredResetPasswordCode(createAt: Instant): Boolean {
        return createAt.plusSeconds(3600).isBefore(Instant.now())
    }

    fun isValidEmail(email: String): Boolean {
        return emailRegex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        return passwordRegex.matches(password)
    }

    fun isValidNickname(nickname: String): Boolean {
        return nickname.isNotEmpty() && nickname.length <= 35
    }

}
