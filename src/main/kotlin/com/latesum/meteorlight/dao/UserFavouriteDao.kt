package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.UserFavourite
import com.latesum.meteorlight.proto.NewsModelProtos.NewsType
import org.springframework.data.jpa.repository.JpaRepository

interface UserFavouriteDao : JpaRepository<UserFavourite?, String> {

    fun findByUserId(userId: String): List<UserFavourite>

    fun findByUserIdAndFavourite(userId: String, favourite: NewsType): UserFavourite?

}
