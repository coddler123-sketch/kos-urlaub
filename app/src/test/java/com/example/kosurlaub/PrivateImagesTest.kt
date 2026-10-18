package com.example.kosurlaub

import com.example.kosurlaub.ui.home.PrivateImages
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Testvektor aus tools/make_access_code.py (encrypt mit 1000 Iterationen), damit App und Skript zusammenpassen. */
class PrivateImagesTest {
    private fun hex(s: String) = s.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    private val salt = hex("000102030405060708090a0b0c0d0e0f")
    private val check = hex("9993409bc4b9f05d")
    private val data = hex(
        "861333008f18e815dcc767c6123a443505f1d7bf3d43e69174a7db57d83e86125bd36b411590fb629026716ddcf9aa5c6ff06cc50d7e50a6bfe1"
    )

    @Test
    fun correctCodeDecryptsToken_ignoringCaseAndDashes() {
        assertEquals(
            "github_pat_TESTTOKEN_1234567890_abcdefghijklmnopqrstuvwxyz",
            PrivateImages.decryptToken("abcd-2345-ef", salt, 1000, check, data)
        )
    }

    @Test
    fun wrongCodeReturnsNull() {
        assertNull(PrivateImages.decryptToken("AAAA-AAAA-AA", salt, 1000, check, data))
    }
}
