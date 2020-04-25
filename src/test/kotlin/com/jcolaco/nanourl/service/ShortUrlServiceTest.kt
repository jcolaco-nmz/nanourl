package com.jcolaco.nanourl.service

import com.jcolaco.nanourl.exceptions.InvalidUrlException
import com.jcolaco.nanourl.exceptions.UrlTooLongException
import com.jcolaco.nanourl.model.ShortUrl
import com.jcolaco.nanourl.model.ShortUrlStatistics
import com.jcolaco.nanourl.repository.ShortUrlAccessLogRepository
import com.jcolaco.nanourl.repository.ShortUrlRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import java.math.BigInteger
import java.util.Optional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ShortUrlServiceTest {

    @Mock
    private lateinit var shortUrlRepository: ShortUrlRepository
    @Mock
    private lateinit var shortUrlAccessLogRepository: ShortUrlAccessLogRepository
    private val maxLength = 50
    private lateinit var subject: ShortUrlService

    @BeforeEach
    fun setup() {
        subject = ShortUrlService(shortUrlRepository, shortUrlAccessLogRepository, maxLength)
    }

    @Test
    fun `create - should throw exception when url is invalid (bad schema)`() {
        assertThrows<InvalidUrlException> { subject.create("ftp://www.foo.com") }
    }

    @Test
    fun `create - should throw exception when url is invalid (bad hostname)`() {
        assertThrows<InvalidUrlException> { subject.create("http://www") }
    }

    @Test
    fun `create - should throw exception when url is invalid (invalid chars)`() {
        assertThrows<InvalidUrlException> { subject.create("http://www.google.com/<script>") }
    }

    @Test
    fun `create - should throw exception when url is invalid (too long)`() {
        assertThrows<UrlTooLongException> { subject.create("http://www.google.com/${"a".repeat(maxLength + 1)}") }
    }

    @Test
    fun `create - should call repository when url is valid`() {
        val short = ShortUrl(BigInteger.ONE, "http://www.google.com/abc")
        whenever(shortUrlRepository.save(any<ShortUrl>())).thenReturn(short)
        assertEquals(short, subject.create(short.url!!))

        verify(shortUrlRepository, times(1)).save(any<ShortUrl>())
    }

    @Test
    fun `findStatistics - should call repository`() {
        val id = BigInteger.TEN
        val stat = ShortUrlStatistics(1, 5, 6)
        whenever(shortUrlAccessLogRepository.countAccesses(id)).thenReturn(stat)
        assertEquals(stat, subject.findStatistics(id))

        verify(shortUrlAccessLogRepository, times(1)).countAccesses(id)
    }

    @Test
    fun `findShortUrl - should return null if not found`() {
        val short = ShortUrl(BigInteger.ONE, "http://www.google.com/abc")
        whenever(shortUrlRepository.findById(short.id!!)).thenReturn(Optional.empty())
        assertNull(subject.findShortUrl(short.id!!))
    }

    @Test
    fun `findShortUrl - should return if found`() {
        val short = ShortUrl(BigInteger.ONE, "http://www.google.com/abc")
        whenever(shortUrlRepository.findById(short.id!!)).thenReturn(Optional.of(short))
        assertEquals(short, subject.findShortUrl(short.id!!))
    }
}
