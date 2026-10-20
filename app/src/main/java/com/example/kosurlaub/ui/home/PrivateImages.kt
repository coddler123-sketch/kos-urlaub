package com.example.kosurlaub.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableIntStateOf
import com.example.kosurlaub.BuildConfig
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.net.HttpURLConnection
import java.net.URL

/**
 * Nicht lizenzfreie Fotos aus dem privaten Repo kos-urlaub-bilder. Sie liegen weder im
 * oeffentlichen Repo noch in der APK: Mit einem Lese-Token (Zugangscode) werden sie einmal
 * geladen und lokal gespeichert. Ohne Code oder Dateien bleiben die freien Bilder aktiv.
 */
object PrivateImages {
    private const val REPO = "coddler123-sketch/kos-urlaub-bilder"
    private const val PREFS = "kos_private" // vom Backup ausgenommen, siehe backup_rules.xml
    private const val KEY_TOKEN = "token"
    private const val ACCESS_URL =
        "https://raw.githubusercontent.com/coddler123-sketch/kos-urlaub/main/bilder-zugang.json"
    private const val MAX_CODE_LENGTH = 20

    /** Wird nach Laden/Entfernen erhoeht, damit Compose-Bilder neu aufgeloest werden. */
    val version = mutableIntStateOf(0)

    val TIPS = mapOf(
        "hotel-ilios-k-village" to "hotel-und-alte-webp/hotel_ilios_village.webp",
        "trip-kos-town" to "kosurlaub-de/07.jpg",
        "trip-asklepieion" to "kosurlaub-de/08.jpg",
        "trip-zia-sunset" to "kosurlaub-de/02.jpg",
        "beach-tigaki" to "kosurlaub-de/04.jpg",
        "beach-marmari" to "kosurlaub-de/12.jpg",
        "tigaki-alikes-salt-lake" to "kosurlaub-de/03.jpg",
        "food-aliada" to "restaurants/aliada/1.jpg",
        "food-brusko" to "restaurants/brusko/1.jpg",
        "food-nisiotiko" to "restaurants/nisiotiko/1.jpg",
        "food-tsambala" to "restaurants/tsambala/g1.jpg",
        "food-alikes" to "restaurants/alikes/9.jpg",
        "food-ikaros" to "restaurants/ikaros/i1.jpg",
    )

    /** Zusaetzliche Tagesbilder fuer Startseite und Widget (10 fehlt bewusst: fast leeres Bild). */
    val ROTATION = listOf(1, 5, 6, 11, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)
        .map { "kosurlaub-de/%02d.jpg".format(it) }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean = prefs(context).getString(KEY_TOKEN, null) != null

    fun fileCount(context: Context): Int =
        File(context.filesDir, "private").walkTopDown().count { it.isFile }

    private fun file(context: Context, path: String) = File(context.filesDir, "private/$path")

    fun has(context: Context, path: String): Boolean = file(context, path).isFile

    /** Verkleinert geladenes Bild oder null, wenn die Datei fehlt. */
    fun bitmap(context: Context, path: String, maxWidth: Int): Bitmap? {
        val f = file(context, path)
        if (!f.isFile) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(f.path, bounds)
        val options = BitmapFactory.Options().apply {
            inSampleSize = (bounds.outWidth / maxWidth).coerceAtLeast(1)
        }
        return BitmapFactory.decodeFile(f.path, options)
    }

    /** Nur Tipp-Bilder, die schon heruntergeladen sind. */
    fun tipBitmap(context: Context, tipId: String, maxWidth: Int = 1200): Bitmap? =
        TIPS[tipId]?.let { bitmap(context, it, maxWidth) }

    /**
     * Wandelt die Eingabe in das GitHub-Token: kurzer Code (z. B. KOS7-H3Q9-XPM2) wird gegen die
     * verschluesselte bilder-zugang.json aufgeloest, ein langes Token gilt direkt.
     */
    private fun resolveToken(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.length > MAX_CODE_LENGTH) return trimmed
        return runCatching {
            val json = JSONObject(String(fetch(ACCESS_URL, null, "application/json")!!))
            decryptToken(
                code = trimmed,
                salt = json.getString("salt").hexToBytes(),
                iterations = json.getInt("iterations"),
                check = json.getString("check").hexToBytes(),
                data = json.getString("data").hexToBytes()
            )
        }.getOrNull()
    }

    /** Muss zu tools/make_access_code.py passen. Null bei falschem Code. */
    internal fun decryptToken(code: String, salt: ByteArray, iterations: Int, check: ByteArray, data: ByteArray): String? {
        val normalized = code.uppercase().filter { it.isLetterOrDigit() }
        val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            .generateSecret(PBEKeySpec(normalized.toCharArray(), salt, iterations, 256)).encoded
        val sha = MessageDigest.getInstance("SHA-256")
        if (!sha.digest(key + "chk".toByteArray()).copyOf(check.size).contentEquals(check)) return null
        val stream = (0..data.size / 32).flatMap { sha.digest(key + byteArrayOf(it.toByte())).asList() }
        return String(ByteArray(data.size) { (data[it].toInt() xor stream[it].toInt()).toByte() })
    }

    private fun String.hexToBytes() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    /**
     * Loest Code/Token auf, prueft es, laedt fehlende Bilder und speichert das Token erst bei Erfolg.
     * Blockierend, nur im Hintergrund-Thread aufrufen.
     * @return Anzahl vorhandener Bilder; null bei ungueltigem Code oder fehlendem Netz.
     */
    fun download(context: Context, codeOrToken: String): Int? {
        val token = resolveToken(codeOrToken) ?: return null
        val tree = fetch("https://api.github.com/repos/$REPO/git/trees/main?recursive=1", token, "application/vnd.github+json")
            ?: return null
        val entries = JSONObject(String(tree)).getJSONArray("tree")
        val paths = (0 until entries.length()).map { entries.getJSONObject(it) }
            .filter { it.optString("type") == "blob" }
            .map { it.getString("path") }
            .filter { it.endsWith(".jpg") || it.endsWith(".webp") }
        prefs(context).edit().putString(KEY_TOKEN, token).apply()
        paths.filterNot { has(context, it) }.forEach { path ->
            val bytes = fetch("https://api.github.com/repos/$REPO/contents/$path", token, "application/vnd.github.raw")
                ?: return@forEach
            file(context, path).apply { parentFile?.mkdirs() }.writeBytes(bytes)
        }
        return paths.count { has(context, it) }
    }

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_TOKEN).apply()
        File(context.filesDir, "private").deleteRecursively()
    }

    private fun fetch(url: String, token: String?, accept: String): ByteArray? {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 30_000
            setRequestProperty("Accept", accept)
            token?.let { setRequestProperty("Authorization", "Bearer $it") }
            setRequestProperty("User-Agent", "KosUrlaub/${BuildConfig.VERSION_NAME}")
        }
        return try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) connection.inputStream.readBytes() else null
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
