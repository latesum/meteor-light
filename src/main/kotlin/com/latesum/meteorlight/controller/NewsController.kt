package com.latesum.meteorlight.controller

import cn.patest.utils.toJson
import com.latesum.meteorlight.proto.NewsControllerProtos
import com.latesum.meteorlight.proto.NewsServiceProtos
import com.latesum.meteorlight.service.NewsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/api")
open class NewsController {

    @Autowired
    private lateinit var newsService: NewsService

    @RequestMapping(value = "/sessions", method = arrayOf(RequestMethod.POST),
            produces = arrayOf("application/json; charset=utf-8"))
    fun listNews(session: HttpSession,
                 @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
                 @RequestParam(value = "limit", required = false, defaultValue = "20") limit: Int): String {
        val response = newsService.listNews(NewsServiceProtos.ListNewsRequest.newBuilder()
                .setUserId(session.getAttribute("id") as String)
                .setPage(page)
                .setLimit(limit).build())
        // Response.
        return NewsControllerProtos.ListNewsResponse.newBuilder()
                .setTotal(response.total)
                .addAllNews(response.newsList).build().toJson()
    }
}
