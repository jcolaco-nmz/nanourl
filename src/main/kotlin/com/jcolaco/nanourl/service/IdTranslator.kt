package com.jcolaco.nanourl.service

import java.math.BigInteger

interface IdTranslator {
    fun encode(value: BigInteger): String
    fun decode(value: String): BigInteger
}
