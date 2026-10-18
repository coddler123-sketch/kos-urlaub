package com.example.kosurlaub

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class NoRhodesLeftoversTest {
    private val banned = Regex(
        "rhod|rodos|rodou|rodiaki|dimokratiki|lindos|kolymbia|faliraki|tsambika|22410|relax hotel|ktel rodou",
        RegexOption.IGNORE_CASE
    )

    @Test
    fun `no Rhodes leftovers in sources and resources`() {
        val roots = listOf("src/main", "src/test", "src/androidTest").map(::File).filter { it.exists() }
        val hits = roots.flatMap { root ->
            root.walkTopDown()
                .filter { it.isFile && it.extension in setOf("kt", "xml") }
                .filter { it.name != "NoRhodesLeftoversTest.kt" }
                .flatMap { file ->
                    file.readLines().withIndex()
                        .filter { banned.containsMatchIn(it.value) }
                        .map { "${file.path}:${it.index + 1}: ${it.value.trim()}" }
                        .toList()
                }
                .toList()
        }
        assertTrue("Rhodos-Reste gefunden:\n" + hits.joinToString("\n"), hits.isEmpty())
    }
}
