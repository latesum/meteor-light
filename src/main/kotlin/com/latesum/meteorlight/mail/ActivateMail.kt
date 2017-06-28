package com.latesum.meteorlight.mail

import com.google.common.io.Files
import java.io.File
import java.nio.charset.StandardCharsets

object ActivateMail {

    val title = "Account Activation / 账户激活"
    val content: String

    init {
        val file = File(javaClass.classLoader.getResource("template/activate_email.html").file)
        content = Files.toString(file, StandardCharsets.UTF_8)
    }

}
