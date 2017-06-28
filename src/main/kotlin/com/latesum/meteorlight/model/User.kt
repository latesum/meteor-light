package com.latesum.meteorlight.model

import com.latesum.meteorlight.proto.NewsModelProtos.NewsType
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.GenericGenerator
import java.time.Instant
import javax.persistence.*

/**
 * Definition for table user.
 */
@Entity
@DynamicUpdate
@Table(uniqueConstraints = arrayOf(
        UniqueConstraint(name = "uk_email", columnNames = arrayOf("email")),
        UniqueConstraint(name = "uk_nickname", columnNames = arrayOf("nickname"))
))
class User(

        @Id
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid")
        val id: String = "",

        @Column(nullable = false)
        var email: String = "",

        @Column(nullable = false)
        var password: String = "",

        @Column(nullable = false, length = 35)
        var nickname: String = "",

        /**
         * User needs ActivateCode for enabling login.
         */
        @Column(nullable = false)
        var activated: Boolean = false,

        @Column(nullable = true,
                columnDefinition = "TIMESTAMP NULL DEFAULT NULL")
        var lastLoginAt: Instant? = null,

        @Column(nullable = true)
        var favourite: NewsType? = null,

        @Column(nullable = true)
        var lastLookAt: NewsType? = null,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        val createAt: Instant = Instant.EPOCH,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
        val updateAt: Instant = Instant.EPOCH

)
