package com.latesum.meteorlight.model

import com.latesum.meteorlight.proto.NewsModelProtos.NewsType
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * Definition for table news.
 */
@Entity
class News(

        @Id
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid")
        val id: String = "",

        @Column(nullable = false)
        var title: String = "",

        @Column(nullable = false)
        var url: String = "",

        @Column(nullable = true)
        var image: String? = null,

        @Column(nullable = false)
        var time: Instant = Instant.EPOCH,

        @Column(nullable = false)
        var type: NewsType = NewsType.GUONEI,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        val createAt: Instant = Instant.EPOCH,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
        val updateAt: Instant = Instant.EPOCH

)
