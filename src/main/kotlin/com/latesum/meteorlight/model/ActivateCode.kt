package com.latesum.meteorlight.model

import org.hibernate.annotations.GenericGenerator
import org.springframework.security.crypto.keygen.KeyGenerators
import java.time.Instant
import javax.persistence.*

/**
 * Definition for table activate_code.
 * User account can be activated by his activate code, which is usually sent to user by email along with user id.
 */
@Entity
@Table(uniqueConstraints = arrayOf(
        UniqueConstraint(name = "uk_user_id", columnNames = arrayOf("user_id"))
))
class ActivateCode(

        @Id
        @GeneratedValue(generator = "system-uuid")
        @GenericGenerator(name = "system-uuid", strategy = "uuid")
        val id: String = "",

        /**
         * The user code belongs to.
         */
        @OneToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(nullable = false, insertable = true, updatable = false,
                foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
        val user: User? = null,

        /**
         * The activate code, randomly generated.
         */
        @Column(nullable = false, insertable = true, updatable = false)
        val code: String = KeyGenerators.string().generateKey(),

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        val createAt: Instant = Instant.EPOCH,

        @Column(nullable = false, insertable = false, updatable = false,
                columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
        val updateAt: Instant = Instant.EPOCH

)
