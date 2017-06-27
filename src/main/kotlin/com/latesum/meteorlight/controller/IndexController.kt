package com.latesum.meteorlight.controller

import com.google.common.io.Files
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpSession

@RestController
open class IndexController {

    lateinit var indexContent: String

    init {
        val filename = javaClass.classLoader.getResource("static/index.html")!!.file
        indexContent = Files.toString(File(filename), StandardCharsets.UTF_8)
    }

    @RequestMapping("/", "/{path:[^.]*}/**")
    fun index(session: HttpSession): String {
        val userId = session.getAttribute("id") as Long?
        return indexContent
    }

}
