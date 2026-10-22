package com.example.kosurlaub.ui.lists

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import java.util.Calendar
import java.util.Locale

/** Lesbare Angaben aus dem Pflichtteil eines IATA-Bordkarten-Codes (BCBP). */
data class BoardingData(
    val passenger: String,
    val bookingRef: String,
    val from: String,
    val to: String,
    val flight: String,
    val dayOfYear: Int,
    val seat: String
)

object Bcbp {
    /** Liest den festen Pflichtteil (60 Zeichen) des ersten Flugabschnitts, oder null, wenn der Text kein BCBP ist. */
    fun parse(payload: String): BoardingData? {
        if (payload.length < 58 || payload[0] != 'M') return null
        fun field(from: Int, to: Int) = payload.substring(from, to).trim()
        val doy = field(44, 47).toIntOrNull() ?: return null
        if (doy !in 1..366) return null
        return BoardingData(
            passenger = prettyName(field(2, 22)),
            bookingRef = field(23, 30),
            from = field(30, 33),
            to = field(33, 36),
            flight = field(36, 39) + " " + field(39, 44).trimStart('0'),
            dayOfYear = doy,
            seat = field(48, 52).trimStart('0')
        )
    }

    /** "MUELLER/ANNA MRS" wird zu "Anna Mueller". */
    fun prettyName(raw: String): String {
        val parts = raw.split("/", limit = 2)
        val first = parts.getOrNull(1)?.trim()?.removeSuffix(" MRS")?.removeSuffix(" MR")?.removeSuffix(" MS").orEmpty()
        return listOf(first, parts[0].trim()).filter { it.isNotEmpty() }
            .joinToString(" ") { it.lowercase(Locale.GERMAN).replaceFirstChar { c -> c.titlecase(Locale.GERMAN) } }
    }

    /** Tag im Jahr als "13.09."; das Jahr entscheidet nur über Schaltjahre. */
    fun dayMonth(dayOfYear: Int, year: Int): String {
        val cal = Calendar.getInstance().apply { clear(); set(Calendar.YEAR, year); set(Calendar.DAY_OF_YEAR, dayOfYear) }
        return String.format(Locale.GERMAN, "%02d.%02d.", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1)
    }
}

/** Barcode aus einem Bild lesen und wieder scharf zeichnen. Alles offline mit ZXing. */
object BarcodeCodec {
    private val formats = listOf(BarcodeFormat.AZTEC, BarcodeFormat.PDF_417, BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX)

    /** Inhalt und Formatname des ersten gefundenen Barcodes, sonst null. */
    fun decode(bitmap: Bitmap): Pair<String, String>? {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
        val hints = mapOf(DecodeHintType.POSSIBLE_FORMATS to formats, DecodeHintType.TRY_HARDER to true)
        for (binarizer in listOf({ HybridBinarizer(source) }, { GlobalHistogramBinarizer(source) })) {
            val result = runCatching { MultiFormatReader().decode(BinaryBitmap(binarizer()), hints) }.getOrNull()
            if (result != null) return result.text to result.barcodeFormat.name
        }
        return null
    }

    /** Schwarz auf Weiß mit Rand, damit Scanner am Gate den Code sicher lesen. */
    fun encode(payload: String, formatName: String, width: Int): Bitmap? = runCatching {
        val format = BarcodeFormat.valueOf(formatName)
        val square = format == BarcodeFormat.AZTEC || format == BarcodeFormat.QR_CODE || format == BarcodeFormat.DATA_MATRIX
        val height = if (square) width else width / 3
        val matrix = MultiFormatWriter().encode(payload, format, width, height, mapOf(EncodeHintType.MARGIN to 2))
        val bmp = Bitmap.createBitmap(matrix.width, matrix.height, Bitmap.Config.ARGB_8888)
        for (x in 0 until matrix.width) for (y in 0 until matrix.height) {
            bmp.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
        }
        bmp
    }.getOrNull()
}
