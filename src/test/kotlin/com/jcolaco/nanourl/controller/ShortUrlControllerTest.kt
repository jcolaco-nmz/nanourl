package com.jcolaco.nanourl.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jcolaco.nanourl.controller.dto.UrlDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@ExtendWith(SpringExtension::class)
class ShortUrlControllerTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `create - should return 200 OK with shortened url`() {
        mvc.perform(post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(mapOf("url" to "http://www.foo.com")))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.url").exists())
    }

    @Test
    fun `create - should return 400 BAD_REQUEST with invalid url request`() {
        mvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(mapOf("url" to "http://www.foo.com/<script>")))
            )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create - should return 413 PAYLOAD_TOO_LARGE with invalid url request`() {
        mvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(mapOf("url" to "http://www.foo.com/${"a".repeat(5000)}")))
            )
            .andExpect(status().isPayloadTooLarge)
    }

    @Test
    fun `get short url should return 404 NOT_FOUND with inexistent id`() {
        mvc.perform(get("/g4gedwfdgsrg3t34g---121"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get short urlshould return shortened url`() {
        val originalUrl = "http://www.foo.com/foo"
        val response = mvc.perform(post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(mapOf("url" to originalUrl)))
        ).andReturn().response.contentAsString

        val url = objectMapper.readValue<UrlDto>(response).url

        mvc.perform(get(url))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", originalUrl))
    }

    @Test
    fun `get short url should create access log`() {
        val originalUrl = "http://www.foo.com/foo"
        val response = mvc.perform(post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(mapOf("url" to originalUrl)))
        ).andReturn().response.contentAsString

        val url = objectMapper.readValue<UrlDto>(response).url

        mvc.perform(get(url))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", originalUrl))

        mvc.perform(get("$url/stats")
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.past_day_total").value(1))
            .andExpect(jsonPath("$.past_week_total").value(1))
            .andExpect(jsonPath("$.all_time_total").value(1))
    }

    private fun asJsonString(obj: Any?): String = try {
        ObjectMapper().writeValueAsString(obj)
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}
