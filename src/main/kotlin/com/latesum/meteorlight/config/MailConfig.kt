package com.latesum.meteorlight.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.mail")
object MailConfig {

    var username: String = ""

}
