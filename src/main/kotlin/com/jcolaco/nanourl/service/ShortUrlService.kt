package com.jcolaco.nanourl.service

import com.jcolaco.nanourl.exceptions.InvalidUrlException
import com.jcolaco.nanourl.exceptions.UrlTooLongException
import com.jcolaco.nanourl.model.ShortUrl
import com.jcolaco.nanourl.model.ShortUrlAccessLog
import com.jcolaco.nanourl.model.ShortUrlStatistics
import com.jcolaco.nanourl.repository.ShortUrlAccessLogRepository
import com.jcolaco.nanourl.repository.ShortUrlRepository
import java.math.BigInteger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.validator.routines.UrlValidator
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ShortUrlService(
    private val shortUrlRepository: ShortUrlRepository,
    private val shortUrlAccessLogRepository: ShortUrlAccessLogRepository,
    @Value("\${url.max.length:2000}") private val maxLength: Int
) {
    private val validSchemas = arrayOf("http", "https")
    private val validator = UrlValidator(validSchemas)

    @CachePut(value = ["shortUrlCache"])
    fun create(url: String): ShortUrl = when {
        !validator.isValid(url) -> throw InvalidUrlException()
        url.length > maxLength -> throw UrlTooLongException()
        else -> shortUrlRepository.save(ShortUrl(url = url))
    }

    fun findShortUrl(id: BigInteger): ShortUrl? = getCacheableShortUrl(id)?.also {
        // Fire and forget
        GlobalScope.launch {
            shortUrlAccessLogRepository.save(ShortUrlAccessLog(shortUrlId = id))
        }
    }

    fun findStatistics(id: BigInteger): ShortUrlStatistics? = shortUrlAccessLogRepository.countAccesses(id)

    @Cacheable(
        value = ["shortUrlCache"],
        key = "#id",
        unless = "#result == null"
    )
    private fun getCacheableShortUrl(id: BigInteger): ShortUrl? = shortUrlRepository.findByIdOrNull(id)
}
