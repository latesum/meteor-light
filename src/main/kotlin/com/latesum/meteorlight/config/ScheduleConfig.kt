package com.latesum.meteorlight.config

import com.latesum.meteorlight.crawler.CrawlerSchedule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class ScheduleConfig {

    @Bean
    fun bean(): CrawlerSchedule {
        return CrawlerSchedule()
    }

}
