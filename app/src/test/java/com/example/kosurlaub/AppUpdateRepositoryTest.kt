package com.example.kosurlaub

import com.example.kosurlaub.ui.settings.AppUpdateRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUpdateRepositoryTest {
    @Test
    fun `newer version is detected per part`() {
        assertTrue(AppUpdateRepository.isNewerVersion("1.0.1", "1.0.0"))
        assertTrue(AppUpdateRepository.isNewerVersion("1.10.0", "1.9.9"))
        assertTrue(AppUpdateRepository.isNewerVersion("2.0", "1.9.9"))
        assertFalse(AppUpdateRepository.isNewerVersion("1.0.0", "1.0.0"))
        assertFalse(AppUpdateRepository.isNewerVersion("1.0.0", "1.0.1"))
    }

    @Test
    fun `version names must be numeric`() {
        assertTrue(AppUpdateRepository.isValidVersionName("1.2.3"))
        assertFalse(AppUpdateRepository.isValidVersionName("1.2.3; rm -rf"))
        assertFalse(AppUpdateRepository.isValidVersionName(""))
    }

    @Test
    fun `only apk downloads from our own release path are trusted`() {
        val base = "https://github.com/coddler123-sketch/kos-urlaub/releases/download/v1.0.1"
        assertTrue(AppUpdateRepository.isTrustedApkDownloadUrl("$base/Kos-Urlaub-1.0.1.apk"))
        assertFalse(AppUpdateRepository.isTrustedApkDownloadUrl("$base/notes.txt"))
        assertFalse(AppUpdateRepository.isTrustedApkDownloadUrl("http://github.com/coddler123-sketch/kos-urlaub/releases/download/v1/a.apk"))
        assertFalse(AppUpdateRepository.isTrustedApkDownloadUrl("https://evil.example/coddler123-sketch/kos-urlaub/releases/download/v1/a.apk"))
        assertFalse(AppUpdateRepository.isTrustedApkDownloadUrl("https://github.com/someone/else/releases/download/v1/a.apk"))
    }

    @Test
    fun `apk content types are limited`() {
        assertTrue(AppUpdateRepository.isAllowedApkContentType("application/vnd.android.package-archive"))
        assertTrue(AppUpdateRepository.isAllowedApkContentType("application/octet-stream; charset=binary"))
        assertFalse(AppUpdateRepository.isAllowedApkContentType("text/html"))
        assertFalse(AppUpdateRepository.isAllowedApkContentType(null))
    }
}
