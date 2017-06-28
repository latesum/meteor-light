package com.latesum.meteorlight.config

import com.latesum.meteorlight.dao.NewsDao
import com.latesum.meteorlight.service.CrawlerService
import com.latesum.meteorlight.util.CommonUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class ScheduleConfig {

    @Autowired
    private lateinit var commonUtil: CommonUtil

    @Autowired
    private lateinit var newsDao: NewsDao

    @Bean
    fun bean(): CrawlerService {
        return CrawlerService(newsDao,commonUtil)
    }

}
