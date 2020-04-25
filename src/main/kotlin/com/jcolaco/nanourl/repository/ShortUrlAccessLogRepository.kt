package com.jcolaco.nanourl.repository

import com.jcolaco.nanourl.model.ShortUrlAccessLog
import com.jcolaco.nanourl.model.ShortUrlStatistics
import java.math.BigInteger
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ShortUrlAccessLogRepository : CrudRepository<ShortUrlAccessLog, BigInteger> {
    @Query(
        value = """
            select 
            count(case when timestamp between (NOW() - interval '1 day') AND NOW() then 1 end) past_day,
            count(case when timestamp between (NOW() - interval '1 week') AND NOW() then 1 end) past_week,
            count(*) total
            from short_url_access_log
            where short_url_id = :shortUrlId
            group by short_url_id
            """
    )
    fun countAccesses(@Param("shortUrlId") shortUrlId: BigInteger): ShortUrlStatistics
}
