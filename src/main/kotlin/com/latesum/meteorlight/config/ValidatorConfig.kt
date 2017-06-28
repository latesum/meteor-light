package com.latesum.meteorlight.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class ValidatorConfig {

    @Value("\${validator.imagecaptcha.login}")
    var loginImageCaptcha: Boolean = false
    @Value("\${validator.imagecaptcha.register}")
    var registerImageCaptcha: Boolean = false
    @Value("\${validator.imagecaptcha.forgetpassword}")
    var forgetPasswordImageCaptcha: Boolean = false
    @Value("\${validator.submission.limit}")
    var submissionLimitation: Int = 0
    @Value("\${validator.login.protection}")
    var loginProtection: Boolean = false

}
