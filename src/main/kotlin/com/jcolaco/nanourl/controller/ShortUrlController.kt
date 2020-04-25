package com.jcolaco.nanourl.controller

import com.jcolaco.nanourl.controller.dto.StatisticsDto
import com.jcolaco.nanourl.controller.dto.UrlDto
import com.jcolaco.nanourl.exceptions.InvalidIdEncodingException
import com.jcolaco.nanourl.exceptions.InvalidUrlException
import com.jcolaco.nanourl.exceptions.UrlTooLongException
import com.jcolaco.nanourl.service.IdTranslator
import com.jcolaco.nanourl.service.ShortUrlService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView

@RestController
class ShortUrlController(
    private val idTranslator: IdTranslator,
    private val shortUrlService: ShortUrlService,
    @Value("\${base.url}") private val baseUrl: String
) {

    @PostMapping("/")
    fun create(@RequestBody urlDto: UrlDto) =
        shortUrlService.create(urlDto.url).id?.let { shortUrlId ->
            ResponseEntity.ok(
                UrlDto("$baseUrl/${idTranslator.encode(shortUrlId)}")
            )
        } ?: throw IllegalStateException()

    @GetMapping("/{id}")
    fun redirect(@PathVariable id: String) =
        shortUrlService.findShortUrl(idTranslator.decode(id))?.let {
            val rv = RedirectView()
            rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY)
            rv.url = it.url
            ModelAndView(rv)
        } ?: notFound()

    @GetMapping("/{id}/stats")
    fun statistics(@PathVariable id: String) =
        shortUrlService.findStatistics(idTranslator.decode(id))?.let {
            ResponseEntity.ok(StatisticsDto(
                url = "$baseUrl/$id",
                pastDayTotal = it.pastDay,
                pastWeekTotal = it.pastWeek,
                allTimeTotal = it.total
            ))
        } ?: notFound()

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InvalidIdEncodingException::class)
    private fun handleInvalidEncodingException() {}

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid URL")
    @ExceptionHandler(InvalidUrlException::class)
    private fun handleInvalidUrlException() {}

    @ResponseStatus(code = HttpStatus.PAYLOAD_TOO_LARGE, reason = "URL bigger than max acceptable")
    @ExceptionHandler(UrlTooLongException::class)
    private fun handleUrlTooLongException() {}

    private fun notFound(): Nothing = throw ResponseStatusException(HttpStatus.NOT_FOUND, "Short Url not found")
}
