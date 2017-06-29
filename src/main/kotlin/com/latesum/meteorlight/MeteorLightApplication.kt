package com.latesum.meteorlight

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer

@SpringBootApplication
open class MeteorLightApplication : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(MeteorLightApplication::class.java)
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(MeteorLightApplication::class.java, *args)
}
