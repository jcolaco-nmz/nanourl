package com.jcolaco.nanourl.model

import java.math.BigInteger
import java.time.Instant
import org.springframework.data.annotation.Id

data class ShortUrlAccessLog(
    @Id
    var id: BigInteger? = null,
    var shortUrlId: BigInteger? = null,
    var timestamp: Instant = Instant.now()
)
