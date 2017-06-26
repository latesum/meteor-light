package com.latesum.meteorlight

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class MeteorLightApplication

fun main(args: Array<String>) {
    SpringApplication.run(MeteorLightApplication::class.java, *args)
}
