package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.News
import com.latesum.meteorlight.proto.NewsModelProtos.NewsType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NewsDao : JpaRepository<News?, String> {

    fun findByType(type: NewsType, pageable: Pageable): List<News>

    fun findByurl(url:String): News?

}
