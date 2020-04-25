package com.jcolaco.nanourl.repository

import com.jcolaco.nanourl.model.ShortUrl
import java.math.BigInteger
import org.springframework.data.repository.CrudRepository

interface ShortUrlRepository : CrudRepository<ShortUrl, BigInteger>
