package com.latesum.meteorlight.crawler

import edu.uci.ics.crawler4j.crawler.CrawlConfig
import edu.uci.ics.crawler4j.crawler.CrawlController
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled


class CrawlerSchedule {

    @Scheduled(initialDelay = 1000, fixedRate = 24 * 3600)
    fun startCrawler() {
        val logger = LoggerFactory.getLogger(CrawlController::class.java)

        /*
         * crawlStorageFolder is a folder where intermediate crawl data is
         * stored.
         */
        val crawlStorageFolder = "output"

        /*
         * numberOfCrawlers shows the number of concurrent threads that should
         * be initiated for crawling.
         */
        // val numberOfCrawlers = Integer.parseInt(args[1])
        val numberOfCrawlers = 5

        val config = CrawlConfig()

        config.crawlStorageFolder = crawlStorageFolder

        /*
         * Be polite: Make sure that we don't send more than 1 request per
         * second (1000 milliseconds between requests).
         */
        config.politenessDelay = 1000

        /*
         * You can set the maximum crawl depth here. The default value is -1 for
         * unlimited depth
         */
        config.maxDepthOfCrawling = 2

        /*
         * You can set the maximum number of pages to crawl. The default value
         * is -1 for unlimited number of pages
         */
        config.maxPagesToFetch = 1000

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.isIncludeBinaryContentInCrawling = false


        /*
         * Do you need to set a proxy? If so, you can use:
         * config.setProxyHost("proxyserver.example.com");
         * config.setProxyPort(8080);
         *
         * If your proxy also needs authentication:
         * config.setProxyUsername(username); config.getProxyPassword(password);
         */

        /*
         * This config parameter can be used to set your crawl to be resumable
         * (meaning that you can resume the crawl from a previously
         * interrupted/crashed crawl). Note: if you enable resuming feature and
         * want to start a fresh crawl, you need to delete the contents of
         * rootFolder manually.
         */
        config.isResumableCrawling = false

        /*
         * Instantiate the controller for this crawl.
         */
        val pageFetcher = PageFetcher(config)
        val robotstxtConfig = RobotstxtConfig()
        val robotstxtServer = RobotstxtServer(robotstxtConfig, pageFetcher)
        val controller = CrawlController(config, pageFetcher, robotstxtServer)

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        controller.addSeed("http://www.ics.uci.edu/")
        controller.addSeed("http://www.ics.uci.edu/~lopes/")
        controller.addSeed("http://www.ics.uci.edu/~welling/")

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(NewsCrawler::class.java, numberOfCrawlers)
    }

}
