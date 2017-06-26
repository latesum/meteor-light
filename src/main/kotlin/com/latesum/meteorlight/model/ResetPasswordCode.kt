package com.latesum.meteorlight.model

import org.hibernate.annotations.GenericGenerator
import org.springframework.security.crypto.keygen.KeyGenerators
import java.time.Instant
import javax.persistence.*

@Entity
@Table(indexes = arrayOf(
        Index(name = "idx_user_id", columnList = "user_id")
))
class ResetPasswordCode(

        @Id
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid")
        val id: Long = 0,

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(nullable = false, insertable = true, updatable = false,
                foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
        val user: User? = null,

        @Column(nullable = false, insertable = true, updatable = false)
        val code: String = KeyGenerators.string().generateKey(),

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        val createAt: Instant = Instant.EPOCH,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
        val updateAt: Instant = Instant.EPOCH

)
