package com.latesum.meteorlight.controller

import cn.patest.utils.toJson
import com.google.common.io.Files
import com.latesum.meteorlight.proto.UserServiceProtos
import com.latesum.meteorlight.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpSession

@RestController
open class IndexController {

    lateinit var indexContent: String

    @Autowired
    private lateinit var userService: UserService

    init {
        val filename = javaClass.classLoader.getResource("static/index.html")!!.file
        indexContent = Files.toString(File(filename), StandardCharsets.UTF_8)
    }

    @RequestMapping("/", "/{path:[^.]*}/**")
    fun index(session: HttpSession): String {
        val userId = session.getAttribute("id") as String?
        val response = if (userId != null)
            userService.getUser(UserServiceProtos.GetUserRequest.newBuilder()
                    .setUserId(userId).build())
        else UserServiceProtos.GetUserResponse.newBuilder().build()
        return indexContent
    }

    @RequestMapping(value = "/api/users", method = arrayOf(RequestMethod.GET),
            produces = arrayOf("application/json; charset=utf-8"))
    fun getUser(session: HttpSession): String {
        val userId = session.getAttribute("id") as String?
        val response = if (userId != null)
            userService.getUser(UserServiceProtos.GetUserRequest.newBuilder()
                    .setUserId(userId).build())
        else UserServiceProtos.GetUserResponse.newBuilder().build()
        return response.toJson()
    }

}
