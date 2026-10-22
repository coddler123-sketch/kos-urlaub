package com.example.kosurlaub.ui.lists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.kosurlaub.TripData
import java.io.File
import java.util.UUID

/**
 * Eine abgelegte Bordkarte. Die Datei liegt unter filesDir/passes/<id>.
 * [payload] und [format] stehen nur da, wenn in der Datei ein Barcode gefunden wurde.
 */
data class Pass(
    val id: String,
    val label: String,
    val pdf: Boolean,
    val payload: String? = null,
    val format: String? = null
)

/** Bordkarten als Foto oder PDF, nur auf diesem Gerät (nicht im Backup: nur sharedpref wird gesichert). */
object PassStore {
    private const val PREFS = "kos_passes"
    private const val KEY = "items"

    fun label(leg: String, person: String): String = if (person.isBlank()) leg else "$leg · ${person.trim()}"

    fun encode(items: List<Pass>): String = items.joinToString("\n") {
        val code = it.payload?.toByteArray()?.joinToString("") { b -> "%02x".format(b) }.orEmpty()
        "${it.id}\t${it.label.replace(Regex("[\t\n]"), " ")}\t${if (it.pdf) 1 else 0}\t$code\t${it.format.orEmpty()}"
    }

    private fun fromHex(hex: String): String =
        String(ByteArray(hex.length / 2) { i -> hex.substring(2 * i, 2 * i + 2).toInt(16).toByte() })

    fun decode(raw: String): List<Pass> = raw.lines().mapNotNull {
        val f = it.split("\t")
        if (f.size < 3 || f[0].isBlank()) {
            null
        } else {
            val payload = f.getOrNull(3)?.takeIf { it.isNotEmpty() }?.let { c -> fromHex(c) }
            Pass(f[0], f[1], f[2] == "1", payload, f.getOrNull(4)?.takeIf { it.isNotEmpty() })
        }
    }

    fun load(context: Context): List<Pass> =
        decode(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "").orEmpty())

    private fun save(context: Context, items: List<Pass>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, encode(items)).apply()
    }

    private fun file(context: Context, id: String) = File(context.filesDir, "passes/$id")

    /** Kopiert die gewählte Datei in den privaten Speicher. Null, wenn sie sich nicht lesen lässt. */
    fun add(context: Context, uri: Uri, label: String): List<Pass>? {
        val pdf = context.contentResolver.getType(uri) == "application/pdf"
        val pass = Pass(UUID.randomUUID().toString(), label, pdf)
        val target = file(context, pass.id)
        val copied = runCatching {
            target.parentFile?.mkdirs()
            context.contentResolver.openInputStream(uri)!!.use { input -> target.outputStream().use { input.copyTo(it) } }
        }.isSuccess
        if (!copied || render(context, pass, 200) == null) {
            target.delete()
            return null
        }
        val code = render(context, pass, 1600)?.let { BarcodeCodec.decode(it) }
        val stored = if (code == null) {
            pass
        } else {
            pass.copy(label = PassTiming.autoLabel(code.first) ?: label, payload = code.first, format = code.second)
        }
        return (load(context) + stored).also { save(context, it) }
    }

    fun remove(context: Context, id: String): List<Pass> {
        file(context, id).delete()
        return load(context).filter { it.id != id }.also { save(context, it) }
    }

    /** Bild bzw. erste PDF-Seite, auf [maxWidth] verkleinert. */
    fun render(context: Context, pass: Pass, maxWidth: Int): Bitmap? = runCatching {
        val f = file(context, pass.id)
        if (pass.pdf) {
            ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                PdfRenderer(fd).use { renderer ->
                    renderer.openPage(0).use { page ->
                        val scale = maxWidth.toFloat() / page.width
                        val bmp = Bitmap.createBitmap(maxWidth, (page.height * scale).toInt(), Bitmap.Config.ARGB_8888)
                        bmp.eraseColor(android.graphics.Color.WHITE)
                        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bmp
                    }
                }
            }
        } else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(f.path, bounds)
            BitmapFactory.decodeFile(f.path, BitmapFactory.Options().apply {
                inSampleSize = (bounds.outWidth / maxWidth).coerceAtLeast(1)
            })
        }
    }.getOrNull()
}

/** Ordnet Bordkarten dem Hin- oder Rückflug zu und sagt, wann sie abläuft. */
object PassTiming {
    private fun legOf(data: BoardingData): String? = when {
        data.from == TripData.outbound.fromAirport && data.to == TripData.outbound.toAirport -> "Hin"
        data.from == TripData.inbound.fromAirport && data.to == TripData.inbound.toAirport -> "Rück"
        else -> null
    }

    /** "Hin" oder "Rück": zuerst nach der Strecke im Barcode, sonst nach dem Namen der Karte. */
    fun leg(pass: Pass): String? =
        pass.payload?.let { Bcbp.parse(it) }?.let { legOf(it) }
            ?: when {
                pass.label.startsWith("Hin") -> "Hin"
                pass.label.startsWith("Rück") -> "Rück"
                else -> null
            }

    fun flight(leg: String?): TripData.Flight? = when (leg) {
        "Hin" -> TripData.outbound
        "Rück" -> TripData.inbound
        else -> null
    }

    /** Name der Karte aus dem Barcode, z. B. "Hin · Anna Mueller", oder null ohne lesbaren Barcode. */
    fun autoLabel(payload: String): String? {
        val data = Bcbp.parse(payload) ?: return null
        val leg = legOf(data) ?: return null
        return PassStore.label(leg, data.passenger)
    }

    fun departureMillis(pass: Pass): Long? = flight(leg(pass))?.departureMillis()

    fun isPast(pass: Pass, nowMillis: Long): Boolean = departureMillis(pass)?.let { nowMillis > it } ?: false
}
