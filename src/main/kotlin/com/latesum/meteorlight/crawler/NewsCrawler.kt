package com.latesum.meteorlight.crawler

import edu.uci.ics.crawler4j.crawler.Page
import edu.uci.ics.crawler4j.crawler.WebCrawler
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.url.WebURL
import java.util.regex.Pattern

class NewsCrawler : WebCrawler() {

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    override fun shouldVisit(referringPage: Page, url: WebURL): Boolean {
        val href = url.url.toLowerCase()
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false
        }
        //val href = page.webURL.url.toLowerCase()
        return href.startsWith("http://news.163.com/17/") ||
                href.startsWith("http://news.163.com/uav/17/") ||
                href.startsWith("http://news.163.com/air/17/") ||
                href.startsWith("http://war.163.com/17/")
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    override fun visit(page: Page) {
        val docid = page.webURL.docid
        val url = page.webURL.url
        val domain = page.webURL.domain
        val path = page.webURL.path
        val subDomain = page.webURL.subDomain
        val parentUrl = page.webURL.parentUrl
        val anchor = page.webURL.anchor

        WebCrawler.logger.debug("Docid: {}", docid)
        WebCrawler.logger.info("URL: {}", url)
        WebCrawler.logger.debug("Domain: '{}'", domain)
        WebCrawler.logger.debug("Sub-domain: '{}'", subDomain)
        WebCrawler.logger.debug("Path: '{}'", path)
        WebCrawler.logger.debug("Parent page: {}", parentUrl)
        WebCrawler.logger.debug("Anchor text: {}", anchor)

        if (page.parseData is HtmlParseData) {
            val htmlParseData = page.parseData as HtmlParseData
            val text = htmlParseData.text
            val html = htmlParseData.html
            val links = htmlParseData.outgoingUrls

            WebCrawler.logger.debug("Text length: {}", text.length)
            WebCrawler.logger.debug("Html length: {}", html.length)
            WebCrawler.logger.debug("Number of outgoing links: {}", links.size)
        }

        val responseHeaders = page.fetchResponseHeaders
        if (responseHeaders != null) {
            WebCrawler.logger.debug("Response headers:")
            for (header in responseHeaders) {
                WebCrawler.logger.debug("\t{}: {}", header.name, header.value)
            }
        }

        WebCrawler.logger.debug("=============")
    }

    companion object {
        private val IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$")
    }
}
