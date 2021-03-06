package com.latesum.meteorlight.model

import com.latesum.meteorlight.proto.NewsModelProtos.NewsType
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

/**
 * Definition for table news.
 */
@Entity
@Table(uniqueConstraints = arrayOf(
        UniqueConstraint(name = "uk_url", columnNames = arrayOf("url"))
))
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
        @Enumerated(EnumType.ORDINAL)
        var type: NewsType = NewsType.GUONEI,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        val createAt: Instant = Instant.EPOCH,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
        val updateAt: Instant = Instant.EPOCH

)
