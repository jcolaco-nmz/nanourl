package com.jcolaco.nanourl.service

import com.jcolaco.nanourl.exceptions.InvalidIdEncodingException
import java.math.BigInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AlphabetBasedTranslatorTest {

    private val subject: IdTranslator = AlphabetBasedTranslator()

    @Test
    fun `encoding and decoding should be bijective`() {
        val id = BigInteger("23487651876578765123452345234523458")
        val encoded = subject.encode(id)

        assertEquals(id, subject.decode(encoded))
    }

    @Test
    fun `decoding should throw exception when value has invalid characters`() {
        assertThrows<InvalidIdEncodingException> { subject.decode("foo-------*") }
    }
}
