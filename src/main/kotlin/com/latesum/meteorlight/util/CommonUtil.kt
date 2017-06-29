package com.latesum.meteorlight.util

import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.Instant

@Component
class CommonUtil {
    companion object {
        val PAGE_MAX_VALUE = Int.MAX_VALUE
        val PAGE_DEFAULT_VALUE = 0
        val LIMIT_MAX_VALUE = 200
        val LIMIT_DEFAULT_VALUE = 100
        val AFTER_DEFAULT_VALUE = 0L
        val BEFORE_DEFAULT_VALUE = Long.MAX_VALUE
        val EPSILON = 1e-6
    }

    fun normalizePage(page: Int) = if (page in 0..PAGE_MAX_VALUE) page else PAGE_DEFAULT_VALUE

    fun normalizeLimit(limit: Int) = if (limit in 1..LIMIT_MAX_VALUE) limit else LIMIT_DEFAULT_VALUE

    fun normalizeAfter(after: Long) = if (after >= 0) after else AFTER_DEFAULT_VALUE

    fun normalizeBefore(before: Long) = if (before > 0) before else BEFORE_DEFAULT_VALUE

    fun compareDouble(x: Double, y: Double): Int {
        if (x - y > EPSILON)
            return 1
        if (y - x > EPSILON)
            return -1
        return 0
    }

    fun parseTime(time: String): Instant {
        if (time == "") return Instant.now()
        val format = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
        val date = format.parse(time)
        return date.toInstant()
    }

}
