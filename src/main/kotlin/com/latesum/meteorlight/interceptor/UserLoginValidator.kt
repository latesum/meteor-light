package com.latesum.meteorlight.interceptor

import com.google.common.base.Strings
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserLoginValidator : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any?): Boolean {
        // login, list problem sets
        if ((request.requestURI == "/api/sessions" && request.method == "POST")) {
            return true
        }
        if (Strings.isNullOrEmpty(request.session?.getAttribute("id")?.toString())) {
            response.status = 403
            response.writer.write("{\"error\": \"CLIENT_USER_UNLOGIN\"}")
            return false
        }
        return true
    }

    override fun postHandle(request: HttpServletRequest?, response: HttpServletResponse?,
                            handler: Any?, modelAndView: ModelAndView?) {

    }

    override fun afterCompletion(request: HttpServletRequest?, response: HttpServletResponse?,
                                 handler: Any?, ex: Exception?) {

    }

}
