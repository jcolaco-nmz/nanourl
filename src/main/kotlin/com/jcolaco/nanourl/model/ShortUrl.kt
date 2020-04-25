package com.jcolaco.nanourl.model

import java.math.BigInteger
import org.springframework.data.annotation.Id

data class ShortUrl(
    @Id
    var id: BigInteger? = null,
    var url: String? = null
)
