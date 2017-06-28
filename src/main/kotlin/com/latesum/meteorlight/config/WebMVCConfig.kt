package com.latesum.meteorlight.config

import com.latesum.meteorlight.interceptor.UserLoginValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
open class WebMVCConfig : WebMvcConfigurerAdapter() {

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    @Autowired
    private lateinit var validatorConfig: ValidatorConfig

    override fun addInterceptors(registry: InterceptorRegistry) {
        // Basic login authentication
        if (validatorConfig.loginProtection) {
            registry.addInterceptor(UserLoginValidator())
                    // add all api
                    .addPathPatterns("/api/**")
                    // exclude some api
                    // register
                    .excludePathPatterns("/api/users")
                    // request reset password, reset password, resend activate mail
                    .excludePathPatterns("/api/users/**")
        }
        super.addInterceptors(registry)
    }

}
