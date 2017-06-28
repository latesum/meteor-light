package com.latesum.meteorlight.service

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.latesum.meteorlight.dao.NewsDao
import com.latesum.meteorlight.model.News
import com.latesum.meteorlight.proto.NewsModelProtos.NewsType
import com.latesum.meteorlight.util.CommonUtil
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

@Service
class CrawlerService(private val newsDao: NewsDao,
                     private val commonUtil: CommonUtil) {

    @Scheduled(initialDelay = 1000, fixedRate = 24 * 3600)
    fun startCrawler() {

        println("start crawler")

        val urlList = mutableListOf<Pair<NewsType, String>>()
        urlList.add(Pair(NewsType.GUONEI,
                "http://temp.163.com/special/00804KVA/cm_guonei.js?callback=data_callback"))
        urlList.add(Pair(NewsType.GUOJI,
                "http://temp.163.com/special/00804KVA/cm_guoji.js?callback=data_callback"))
        urlList.add(Pair(NewsType.SHEHUI,
                "http://temp.163.com/special/00804KVA/cm_shehui.js?callback=data_callback"))
        urlList.add(Pair(NewsType.JUNSHI,
                "http://temp.163.com/special/00804KVA/cm_war.js?callback=data_callback"))
        urlList.add(Pair(NewsType.HANGKONG,
                "http://temp.163.com/special/00804KVA/cm_hangkong.js?callback=data_callback"))
        urlList.add(Pair(NewsType.WURENJI,
                "http://news.163.com/uav/special/000189N0/uav_index.js?callback=data_callback"))
        (2..8).map {
            urlList.add(Pair(NewsType.GUONEI,
                    "http://temp.163.com/special/00804KVA/cm_guonei_0$it.js?callback=data_callback"))
            urlList.add(Pair(NewsType.GUOJI,
                    "http://temp.163.com/special/00804KVA/cm_guoji_0$it.js?callback=data_callback"))
            urlList.add(Pair(NewsType.SHEHUI,
                    "http://temp.163.com/special/00804KVA/cm_shehui_0$it.js?callback=data_callback"))
            urlList.add(Pair(NewsType.JUNSHI,
                    "http://temp.163.com/special/00804KVA/cm_war_0$it.js?callback=data_callback"))
            urlList.add(Pair(NewsType.HANGKONG,
                    "http://temp.163.com/special/00804KVA/cm_hangkong_0$it.js?callback=data_callback"))
        }
        (2..5).map {
            urlList.add(Pair(NewsType.WURENJI,
                    "http://news.163.com/uav/special/000189N0/uav_index_0$it.js?callback=data_callback"))
        }

        val parse = JsonParser()

        while (urlList.isNotEmpty()) {
            val url = URL(urlList[0].second)
            val type = urlList[0].first
            urlList.removeAt(0)

            val urlCon = url.openConnection()
            var input = ""
            val buff = BufferedReader(InputStreamReader(urlCon.getInputStream(), "GB2312"))
            var inputLine: String
            while (true) {
                inputLine = buff.readLine() ?: break
                input += inputLine
            }
            buff.close()
            input = input.removePrefix("data_callback(").removeSuffix(")")
            val json = if (parse.parse(input).isJsonArray)
                parse.parse(input).asJsonArray
            else JsonArray()
            json.map {
                if (it.isJsonObject) {
                    val newsObject = it.asJsonObject
                    val news = News(
                            title = newsObject.get("title").asString,
                            url = newsObject.get("docurl").asString,
                            image = newsObject.get("imgurl").asString,
                            time = commonUtil.parseTime(newsObject.get("time").asString),
                            type = type
                    )
                    newsDao.save(news)
                }
            }

            Thread.sleep(2000)

        }

        println("search over")

    }

}
