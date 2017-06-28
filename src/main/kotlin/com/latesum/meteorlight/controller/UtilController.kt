package com.latesum.meteorlight.controller

import com.latesum.meteorlight.proto.UserServiceProtos.ActivateUserRequest
import com.latesum.meteorlight.service.UserService
import io.grpc.StatusRuntimeException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/_")
class UtilController {

    @Autowired
    private lateinit var userService: UserService

    @RequestMapping(value = "/activate", method = arrayOf(RequestMethod.GET))
    fun activate(@RequestParam(value = "id", required = true) id: String,
                 @RequestParam(value = "activatecode", required = true) activateCode: String): String {
        try {
            userService.activateUser(ActivateUserRequest.newBuilder()
                    .setUserId(id)
                    .setActivateCode(activateCode).build())
            return "redirect:/#activated=true"
        } catch (e: StatusRuntimeException) {
            return "redirect:/#activated=false"
        }
    }

}
