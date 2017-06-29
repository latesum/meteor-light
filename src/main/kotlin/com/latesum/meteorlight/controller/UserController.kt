package com.latesum.meteorlight.controller

import cn.patest.utils.ProtobufUtils
import cn.patest.utils.toJson
import com.latesum.meteorlight.mail.ActivateMail
import com.latesum.meteorlight.mail.MailSender
import com.latesum.meteorlight.mail.ResetPasswordMail
import com.latesum.meteorlight.proto.UserControllerProtos
import com.latesum.meteorlight.proto.UserServiceProtos
import com.latesum.meteorlight.service.UserService
import org.apache.commons.lang3.text.StrSubstitutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/api")
open class UserController {

    @Autowired
    private lateinit var mailSender: MailSender

    @Autowired
    private lateinit var userService: UserService

    @RequestMapping(value = "/sessions", method = arrayOf(RequestMethod.POST),
            produces = arrayOf("application/json; charset=utf-8"))
    fun login(session: HttpSession, @RequestPart(value = "data", required = true) data: String): String {
        val request = ProtobufUtils.fromJson(data, UserControllerProtos.LoginRequest::class)

        val response = userService.login(UserServiceProtos.LoginRequest.newBuilder()
                .setEmail(request.email)
                .setPassword(request.password).build())
        session.setAttribute("id", response.user.id)

        // Response.
        return UserControllerProtos.LoginResponse.newBuilder()
                .setUser(response.user).build().toJson()
    }

    @RequestMapping(value = "/sessions", method = arrayOf(RequestMethod.DELETE),
            produces = arrayOf("application/json; charset=utf-8"))
    fun logout(session: HttpSession): String {
        session.removeAttribute("id")
        return "{}"
    }

    @RequestMapping(value = "/users", method = arrayOf(RequestMethod.POST),
            produces = arrayOf("application/json; charset=utf-8"))
    fun register(@RequestPart(value = "data", required = true) data: String): String {
        val request = ProtobufUtils.fromJson(data, UserControllerProtos.RegisterRequest::class)
        val response = userService.addUser(UserServiceProtos.AddUserRequest.newBuilder()
                .setEmail(request.email)
                .setPassword(request.password)
                .setNickname(request.nickname).build())
        // Construct and send activate email to user.
        val mailKey = HashMap<String, String>()
        mailKey.put("id", response.user.id.toString())
        mailKey.put("email", response.user.email)
        mailKey.put("activateCode", response.activateCode)
        mailSender.send(
                to = response.user.email,
                subject = ActivateMail.title,
                body = StrSubstitutor(mailKey).replace(ActivateMail.content)
        )
        // Response.
        return UserControllerProtos.RegisterResponse.newBuilder()
                .setEmail(response.user.email).build().toJson()
    }

    @RequestMapping(value = "/users/{user_id}/password", method = arrayOf(RequestMethod.PATCH),
            produces = arrayOf("application/json; charset=utf-8"))
    fun resetPassword(@PathVariable("user_id") userId: String,
                      @RequestPart(value = "data", required = true) data: String): String {
        val request = ProtobufUtils.fromJson(data, UserControllerProtos.ResetPasswordRequest::class)
        // Reset user password.
        userService.resetPassword(UserServiceProtos.ResetPasswordRequest.newBuilder()
                .setUserId(userId)
                .setResetPasswordCode(request.resetPasswordCode)
                .setNewPassword(request.newPassword).build())
        // Response.
        return UserControllerProtos.ResetPasswordResponse.newBuilder().build().toJson()
    }

    @RequestMapping(value = "/users/password-reset", method = arrayOf(RequestMethod.POST),
            produces = arrayOf("application/json; charset=utf-8"))
    fun requestResetPassword(@RequestPart(value = "data", required = true) data: String): String {
        val request = ProtobufUtils.fromJson(data, UserControllerProtos.RequestResetPasswordRequest::class)

        // Generate reset password code.
        val response = userService.generateResetPasswordCode(UserServiceProtos.GenerateResetPasswordCodeRequest.newBuilder()
                .setEmail(request.email).build())
        // Send reset password email to user.
        val mailKey = HashMap<String, String>()
        mailKey.put("id", response.userId.toString())
        mailKey.put("email", request.email)
        mailKey.put("resetPasswordCode", response.resetPasswordCode)
        mailSender.send(
                to = request.email,
                subject = ResetPasswordMail.title,
                body = StrSubstitutor(mailKey).replace(ResetPasswordMail.content)
        )
        // Response.
        return UserControllerProtos.RequestResetPasswordResponse.newBuilder()
                .setEmail(request.email).build().toJson()
    }

    @RequestMapping(value = "/users/activation", method = arrayOf(RequestMethod.POST),
            produces = arrayOf("application/json; charset=utf-8"))
    fun resendActivateMail(@RequestPart(value = "data", required = true) data: String): String {
        val request = ProtobufUtils.fromJson(data, UserControllerProtos.ResendActivateMailRequest::class)
        // get user activate code.
        val response = userService.getUserActivateCode(UserServiceProtos.GetUserActivateCodeRequest.newBuilder()
                .setEmail(request.email).build())
        // Send activate email to user.
        val mailKey = HashMap<String, String>()
        mailKey.put("id", response.userId.toString())
        mailKey.put("email", request.email)
        mailKey.put("activateCode", response.activateCode)
        mailSender.send(
                to = request.email,
                subject = ActivateMail.title,
                body = StrSubstitutor(mailKey).replace(ActivateMail.content)
        )
        // Response.
        return UserControllerProtos.ResendActivateMailResponse.newBuilder().build().toJson()
    }

    @RequestMapping(value = "/users/favourites", method = arrayOf(RequestMethod.POST),
            produces = arrayOf("application/json; charset=utf-8"))
    fun addUserFavourite(session: HttpSession,
                         @RequestPart(value = "data", required = true) data: String): String {
        val request = ProtobufUtils.fromJson(data, UserControllerProtos.AddUserFavouriteRequest::class)
        val response = userService.addUserFavourite(UserServiceProtos.AddUserFavouriteRequest.newBuilder()
                .setUserId(session.getAttribute("id") as String)
                .setFavourite(request.favourite).build())
        return UserControllerProtos.AddUserFavouriteResponse.newBuilder()
                .setUser(response.user).build().toJson()
    }

    @RequestMapping(value = "/users/favourites", method = arrayOf(RequestMethod.DELETE),
            produces = arrayOf("application/json; charset=utf-8"))
    fun deleteUserFavourite(session: HttpSession,
                            @RequestPart(value = "data", required = true) data: String): String {
        val request = ProtobufUtils.fromJson(data, UserControllerProtos.DeleteUserFavouriteRequest::class)
        val response = userService.removeUserFavourite(UserServiceProtos.DeleteUserFavouriteRequest.newBuilder()
                .setUserId(session.getAttribute("id") as String)
                .setFavourite(request.favourite).build())
        return UserControllerProtos.DeleteUserFavouriteResponse.newBuilder()
                .setUser(response.user).build().toJson()
    }

}
