package com.latesum.meteorlight.crawler

import com.geccocrawler.gecco.GeccoEngine
import com.geccocrawler.gecco.annotation.Gecco
import com.geccocrawler.gecco.annotation.HtmlField
import com.geccocrawler.gecco.annotation.Image
import com.geccocrawler.gecco.annotation.RequestParameter
import com.geccocrawler.gecco.request.HttpGetRequest
import com.geccocrawler.gecco.spider.HtmlBean

@Gecco(matchUrl = arrayOf("http://www.meizitu.com/a/{code}.html"),
        pipelines = arrayOf("consolePipeline", "bigPicPipeline"))
class BigPic : HtmlBean {

    @RequestParameter
    var picInfoId: Int = 0

    @Image
    @HtmlField(cssPath = ".postContent img")
    var pics: List<String> = listOf()

    companion object {

        private val serialVersionUID = 1L

        @JvmStatic fun main(args: Array<String>) {
            val start = HttpGetRequest("http://www.meizitu.com/a/375.html")
            start.charset = "GBK"
            GeccoEngine.create().classpath("com.road.crawler.meizitu").start(start).interval(2000).run()
        }
    }

}
