package cn.patest.insideidentity

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
open class MeteorLightApplication

fun main(args: Array<String>) {
    SpringApplication.run(MeteorLightApplication::class.java, *args)
}
