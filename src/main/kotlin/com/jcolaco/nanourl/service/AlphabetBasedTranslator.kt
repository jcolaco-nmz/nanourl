package com.jcolaco.nanourl.service

import com.jcolaco.nanourl.exceptions.InvalidIdEncodingException
import java.math.BigInteger
import org.springframework.stereotype.Service

@Service
class AlphabetBasedTranslator : IdTranslator {

    private companion object {
        /*
         * Scrambled ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
         * to reduce predictability
         */
        val ALPHABET: CharArray = "0IRdnSrU96GN5eFHQuDMmoqZTpsY2cf8EVBzaJAbWhytP4v31xkjwg7XOKlLiC".toCharArray()
        val BASE: BigInteger = BigInteger.valueOf(ALPHABET.size.toLong())
    }

    override fun encode(value: BigInteger): String {
        var num = value
        val sb = StringBuilder()
        while (num.signum() == 1) {
            sb.append(ALPHABET[(num.remainder(BASE)).toInt()])
            num /= BASE
        }

        return sb.reverse().toString()
    }

    override fun decode(value: String): BigInteger {
        var num = BigInteger.ZERO
        value.forEach { c ->
            val index = ALPHABET.indexOf(c)

            if (index < 0) throw InvalidIdEncodingException()

            num = num.times(BASE).plus(index.toBigInteger())
        }
        return num
    }
}
