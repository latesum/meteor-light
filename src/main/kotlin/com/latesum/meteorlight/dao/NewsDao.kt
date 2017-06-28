package com.latesum.meteorlight.dao

import com.latesum.meteorlight.model.News
import org.springframework.data.jpa.repository.JpaRepository

interface NewsDao : JpaRepository<News?, Long>
