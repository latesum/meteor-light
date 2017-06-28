package com.latesum.meteorlight.service

import com.google.protobuf.Timestamp
import com.latesum.meteorlight.dao.NewsDao
import com.latesum.meteorlight.dao.UserDao
import com.latesum.meteorlight.exception.ServiceException
import com.latesum.meteorlight.proto.ErrorProtos.Error
import com.latesum.meteorlight.proto.NewsModelProtos.NewsProfile
import com.latesum.meteorlight.proto.NewsServiceProtos.ListNewsRequest
import com.latesum.meteorlight.proto.NewsServiceProtos.ListNewsResponse
import com.latesum.meteorlight.util.CommonUtil
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewsService(private val userDao: UserDao,
                  private val newsDao: NewsDao,
                  private val commonUtil: CommonUtil) {

    @Throws(ServiceException::class)
    @Transactional(readOnly = true)
    fun listNews(request: ListNewsRequest): ListNewsResponse {
        val user = userDao.findOne(request.userId) ?:
                throw ServiceException.newBuilder()
                        .setType(ServiceException.ExceptionType.INVALID_ARGUMENT)
                        .setMessage(Error.USER_NOT_FOUND.name).build()

        val page = commonUtil.normalizePage(request.page)
        val limit = commonUtil.normalizeLimit(request.limit)

        val news = newsDao.findAll(PageRequest(page, limit))
        val total = newsDao.count()

        return ListNewsResponse.newBuilder()
                .setTotal(total.toInt())
                .addAllNews(news.map {
                    NewsProfile.newBuilder()
                            .setId(it!!.id)
                            .setTitle(it.title)
                            .setUrl(it.url)
                            .setImage(it.image)
                            .setTime(Timestamp.newBuilder()
                                    .setSeconds(it.time.epochSecond))
                            .setType(it.type).build()
                }).build()
    }

}
