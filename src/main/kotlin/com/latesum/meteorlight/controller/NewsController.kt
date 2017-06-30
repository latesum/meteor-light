package com.latesum.meteorlight.controller

import cn.patest.utils.toJson
import com.latesum.meteorlight.proto.NewsControllerProtos
import com.latesum.meteorlight.proto.NewsModelProtos
import com.latesum.meteorlight.proto.NewsServiceProtos
import com.latesum.meteorlight.service.NewsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/api/news")
open class NewsController {

    @Autowired
    private lateinit var newsService: NewsService

    @RequestMapping(value = "", method = arrayOf(RequestMethod.GET),
            produces = arrayOf("application/json; charset=utf-8"))
    fun listNews(session: HttpSession,
                 @RequestParam(value = "page", required = false, defaultValue = "0") page: Int,
                 @RequestParam(value = "limit", required = false, defaultValue = "30") limit: Int,
                 @RequestParam(value = "type", required = false, defaultValue = "ALL") type: String): String {
        val response = newsService.listNews(NewsServiceProtos.ListNewsRequest.newBuilder()
                .setUserId(session.getAttribute("id") as String? ?: "")
                .setPage(page)
                .setLimit(limit)
                .setType(NewsModelProtos.NewsType.valueOf(type)).build())
        // Response.
        return NewsControllerProtos.ListNewsResponse.newBuilder()
                .setEnd(response.end)
                .addAllNews(response.newsList).build().toJson()
    }
}
