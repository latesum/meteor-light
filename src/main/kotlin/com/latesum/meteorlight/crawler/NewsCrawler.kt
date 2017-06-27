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

        // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        return href.startsWith("http://www.ics.uci.edu/")
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    override fun visit(page: Page) {
        println(111)
        val docid = page.getWebURL().getDocid()
        val url = page.getWebURL().getURL()
        val domain = page.getWebURL().getDomain()
        val path = page.getWebURL().getPath()
        val subDomain = page.getWebURL().getSubDomain()
        val parentUrl = page.getWebURL().getParentUrl()
        val anchor = page.getWebURL().getAnchor()

        WebCrawler.logger.debug("Docid: {}", docid)
        WebCrawler.logger.info("URL: {}", url)
        WebCrawler.logger.debug("Domain: '{}'", domain)
        WebCrawler.logger.debug("Sub-domain: '{}'", subDomain)
        WebCrawler.logger.debug("Path: '{}'", path)
        WebCrawler.logger.debug("Parent page: {}", parentUrl)
        WebCrawler.logger.debug("Anchor text: {}", anchor)

        if (page.getParseData() is HtmlParseData) {
            val htmlParseData = page.getParseData() as HtmlParseData
            val text = htmlParseData.text
            val html = htmlParseData.html
            val links = htmlParseData.outgoingUrls

            WebCrawler.logger.debug("Text length: {}", text.length)
            WebCrawler.logger.debug("Html length: {}", html.length)
            WebCrawler.logger.debug("Number of outgoing links: {}", links.size)
        }

        val responseHeaders = page.getFetchResponseHeaders()
        if (responseHeaders != null) {
            WebCrawler.logger.debug("Response headers:")
            for (header in responseHeaders) {
                WebCrawler.logger.debug("\t{}: {}", header.getName(), header.getValue())
            }
        }

        WebCrawler.logger.debug("=============")
    }

    companion object {

        private val IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$")
    }
}
