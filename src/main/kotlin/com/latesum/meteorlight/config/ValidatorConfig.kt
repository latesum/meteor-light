package com.latesum.meteorlight.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class ValidatorConfig {

    @Value("\${validator.login}")
    var loginProtection: Boolean = false

}
