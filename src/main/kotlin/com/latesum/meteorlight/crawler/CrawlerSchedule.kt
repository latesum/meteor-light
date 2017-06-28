package com.latesum.meteorlight.crawler

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.springframework.scheduling.annotation.Scheduled
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class CrawlerSchedule {

    @Scheduled(initialDelay = 1000, fixedRate = 24 * 3600)
    fun startCrawler() {
        val urlList = mutableListOf<Pair<String, String>>()
        urlList.add(Pair("guonei",
                "http://temp.163.com/special/00804KVA/cm_guonei.js?callback=data_callback"))
        urlList.add(Pair("guoji",
                "http://temp.163.com/special/00804KVA/cm_guoji.js?callback=data_callback"))
        urlList.add(Pair("shehui",
                "http://temp.163.com/special/00804KVA/cm_shehui.js?callback=data_callback"))
        urlList.add(Pair("junshi",
                "http://temp.163.com/special/00804KVA/cm_war.js?callback=data_callback"))
        urlList.add(Pair("hangkong",
                "http://temp.163.com/special/00804KVA/cm_hangkong.js?callback=data_callback"))
        urlList.add(Pair("wurenji",
                "http://news.163.com/uav/special/000189N0/uav_index.js?callback=data_callback"))
        (2..8).map {
            urlList.add(Pair("guonei",
                    "http://temp.163.com/special/00804KVA/cm_guonei_0$it.js?callback=data_callback"))
            urlList.add(Pair("guoji",
                    "http://temp.163.com/special/00804KVA/cm_guoji_0$it.js?callback=data_callback"))
            urlList.add(Pair("shehui",
                    "http://temp.163.com/special/00804KVA/cm_shehui_0$it.js?callback=data_callback"))
            urlList.add(Pair("junshi",
                    "http://temp.163.com/special/00804KVA/cm_war_0$it.js?callback=data_callback"))
            urlList.add(Pair("hangkong",
                    "http://temp.163.com/special/00804KVA/cm_hangkong_0$it.js?callback=data_callback"))
        }
        (2..5).map {
            urlList.add(Pair("wurenji", "http://news.163.com/uav/special/000189N0/uav_index_0$it.js?callback=data_callback"))
        }

        val parse = JsonParser()

        while (urlList.isNotEmpty()) {
            val url = URL(urlList[0].second)
            val type = urlList[0].first
            urlList.removeAt(0)

            val urlCon = url.openConnection()
            var input = ""
            val buff = BufferedReader(InputStreamReader(urlCon.getInputStream(), "UTF-8"))
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
                if (!it.isJsonObject) println(it.isJsonObject)
            }
            Thread.sleep(1000)

        }

        println("search over")

    }

}
