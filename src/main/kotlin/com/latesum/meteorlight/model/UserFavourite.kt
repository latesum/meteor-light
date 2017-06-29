package com.latesum.meteorlight.model

import com.latesum.meteorlight.proto.NewsModelProtos.NewsType
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

/**
 * Definition for table user_favourite.
 */
@Entity
@Table(indexes = arrayOf(
        Index(name = "idx_user_id", columnList = "user_id")
))
class UserFavourite(

        @Id
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid")
        val id: String = "",

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(nullable = false, insertable = true, updatable = false,
                foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
        val user: User? = null,

        @Column(nullable = false)
        val favourite: NewsType = NewsType.ALL,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        val createAt: Instant = Instant.EPOCH,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
        val updateAt: Instant = Instant.EPOCH

)
