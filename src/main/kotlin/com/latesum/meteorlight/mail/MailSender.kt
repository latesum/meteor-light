package com.latesum.meteorlight.mail

import com.latesum.meteorlight.config.MailConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Component("mailService")
class MailSender {

    @Autowired
    lateinit var mailSender: JavaMailSender

    val pool = ThreadPoolExecutor(0, 16, 10L, TimeUnit.SECONDS, SynchronousQueue<Runnable>())

    fun send(to: String, subject: String, body: String) {
        pool.execute {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            // It needs to set sender for secured SMTP server.
            helper.setFrom(MailConfig.username)
            helper.setSubject(subject)
            helper.setTo(to)
            helper.setText(body, true)
            mailSender.send(message)
        }
    }

}
