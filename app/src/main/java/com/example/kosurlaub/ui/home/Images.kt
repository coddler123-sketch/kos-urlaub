package com.example.kosurlaub.ui.home
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import android.content.Context
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Tagesrotation fuer die Widget-Hintergrundbilder.
 *
 * Die Reihenfolge wurde einmalig mit festem Seed (42) gewuerfelt und ist
 * hier hart kodiert, damit das gleiche Bild bei jedem Build am selben Tag
 * erscheint. Aenderungen an der Reihenfolge wirken sich sofort aus.
 */
object Images {

    private data class ImageAsset(val name: String, val resId: Int)

    private val resources = listOf(
        ImageAsset("kos_rot_01", R.drawable.kos_rot_01),
        ImageAsset("kos_rot_02", R.drawable.kos_rot_02),
        ImageAsset("kos_rot_03", R.drawable.kos_rot_03),
        ImageAsset("kos_rot_04", R.drawable.kos_rot_04),
        ImageAsset("kos_rot_05", R.drawable.kos_rot_05),
        ImageAsset("kos_rot_06", R.drawable.kos_rot_06),
        ImageAsset("kos_rot_07", R.drawable.kos_rot_07),
        ImageAsset("kos_rot_08", R.drawable.kos_rot_08),
        ImageAsset("kos_rot_09", R.drawable.kos_rot_09),
        ImageAsset("kos_rot_10", R.drawable.kos_rot_10),
        ImageAsset("kos_rot_11", R.drawable.kos_rot_11),
        ImageAsset("kos_rot_12", R.drawable.kos_rot_12),
        ImageAsset("kos_rot_13", R.drawable.kos_rot_13),
        ImageAsset("kos_rot_14", R.drawable.kos_rot_14),
        ImageAsset("kos_rot_15", R.drawable.kos_rot_15),
        ImageAsset("kos_rot_16", R.drawable.kos_rot_16),
        ImageAsset("kos_rot_17", R.drawable.kos_rot_17),
        ImageAsset("kos_rot_18", R.drawable.kos_rot_18),
        ImageAsset("kos_rot_19", R.drawable.kos_rot_19),
        ImageAsset("kos_rot_20", R.drawable.kos_rot_20),
        ImageAsset("kos_rot_21", R.drawable.kos_rot_21),
        ImageAsset("kos_rot_22", R.drawable.kos_rot_22),
        ImageAsset("kos_rot_23", R.drawable.kos_rot_23),
        ImageAsset("kos_rot_24", R.drawable.kos_rot_24),
        ImageAsset("kos_rot_25", R.drawable.kos_rot_25),
        ImageAsset("kos_rot_26", R.drawable.kos_rot_26),
        ImageAsset("kos_rot_27", R.drawable.kos_rot_27),
        ImageAsset("kos_rot_28", R.drawable.kos_rot_28),
        ImageAsset("kos_rot_29", R.drawable.kos_rot_29),
        ImageAsset("kos_rot_30", R.drawable.kos_rot_30),
        ImageAsset("kos_rot_31", R.drawable.kos_rot_31),
        ImageAsset("kos_rot_32", R.drawable.kos_rot_32),
        ImageAsset("kos_rot_33", R.drawable.kos_rot_33),
        ImageAsset("kos_rot_34", R.drawable.kos_rot_34),
        ImageAsset("kos_rot_35", R.drawable.kos_rot_35),
        ImageAsset("kos_rot_36", R.drawable.kos_rot_36),
        ImageAsset("kos_rot_37", R.drawable.kos_rot_37),
        ImageAsset("kos_rot_38", R.drawable.kos_rot_38),
        ImageAsset("kos_rot_39", R.drawable.kos_rot_39)
    )

    private val resourceNames = resources.map { it.name }
    private val resourcesByName = resources.associateBy { it.name }

    private const val PREFS_SETTINGS = "kos_settings"
    private const val KEY_PINNED_IMAGE = "pinned_image_name"

    val allImageNames: List<String> = resourceNames.distinct()

    fun getPinnedImage(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PINNED_IMAGE, null)
    }

    fun setPinnedImage(context: Context, name: String?) {
        val prefs = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PINNED_IMAGE, name).apply()
    }

    fun currentImageName(context: Context): String {
        val pinned = getPinnedImage(context)
        if (pinned != null && pinned != "auto" && pinned in allImageNames) return pinned

        return rotationImageName()
    }

    private fun daysSinceEpoch(): Long {
        val now = Calendar.getInstance()
        val localMillis = now.timeInMillis +
            now.get(Calendar.ZONE_OFFSET) + now.get(Calendar.DST_OFFSET)
        return TimeUnit.MILLISECONDS.toDays(localMillis)
    }

    fun rotationImageName(): String {
        val index = (daysSinceEpoch() % resourceNames.size).toInt()
        return resourceNames[index]
    }

    /**
     * Pfad des privaten Tagesbilds, wenn heute eines dran ist (Pool = freie Bilder + geladene
     * private). Null ohne geladene Bilder oder bei festgepinntem Titelbild.
     */
    fun privatePathOfTheDay(context: Context): String? {
        val pinned = getPinnedImage(context)
        if (pinned != null && pinned != "auto" && pinned in allImageNames) return null
        val pool = PrivateImages.ROTATION.filter { PrivateImages.has(context, it) }
        val index = (daysSinceEpoch() % (resourceNames.size + pool.size)).toInt()
        return pool.getOrNull(index - resourceNames.size)
    }

    /** Drawable-Resource fuer das heutige Hintergrundbild. */
    fun resourceOfTheDay(context: Context): Int {
        val name = currentImageName(context)
        return resourceOf(name)
    }

    fun resourceOf(resourceName: String): Int =
        resourcesByName[resourceName]?.resId ?: resources.first().resId

    fun displayNameOf(resourceName: String): String = when (resourceName) {
        "kos_rot_01" -> "Kastri-Insel mit Kapelle"
        "kos_rot_02" -> "Rettungsring am Strand"
        "kos_rot_03" -> "Kapelle bei Sonnenuntergang"
        "kos_rot_04" -> "Sonnenuntergang über dem Meer"
        "kos_rot_05" -> "Luftbild Küste mit Dünen"
        "kos_rot_06" -> "Küstenstraße am Meer"
        "kos_rot_07" -> "Ausflugsschiff und Angler"
        "kos_rot_08" -> "Segelschiff am Pier"
        "kos_rot_09" -> "Hafen von Kos-Stadt"
        "kos_rot_10" -> "Steg bei Sonnenuntergang"
        "kos_rot_11" -> "Ausflugssegler vor Kos"
        "kos_rot_12" -> "Treppengasse mit blauen Stühlen"
        "kos_rot_13" -> "Segelschiff am Hafen"
        "kos_rot_14" -> "Segelschiffe am Pier"
        "kos_rot_15" -> "Abendstimmung in der Taverne"
        "kos_rot_16" -> "Großsegler im Hafen"
        "kos_rot_17" -> "Fischerboot im Hafen von Kos"
        "kos_rot_18" -> "Küste mit Baum"
        "kos_rot_19" -> "Strandpromenade von Kos-Stadt"
        "kos_rot_20" -> "Fischerboot im Hafen"
        "kos_rot_21" -> "Kuppelkirche am Platz"
        "kos_rot_22" -> "Defterdar-Moschee in Kos-Stadt"
        "kos_rot_23" -> "Strand mit griechischer Flagge"
        "kos_rot_24" -> "Blaues Fenster"
        "kos_rot_25" -> "Aussichtsterrasse mit Meerblick"
        "kos_rot_26" -> "Taverne mit blauen Blumentöpfen"
        "kos_rot_27" -> "Segelboote im klaren Wasser"
        "kos_rot_28" -> "Kirche in Kos-Stadt"
        "kos_rot_29" -> "Kirche auf Kos"
        "kos_rot_30" -> "Kirche in Kos-Stadt (Innenhof)"
        "kos_rot_31" -> "Strand bei Tigaki"
        "kos_rot_32" -> "Fischerboot am Strand von Kamari"
        "kos_rot_33" -> "Boot am Strand von Kamari"
        "kos_rot_34" -> "Bucht von Kefalos vom Kamari-Strand"
        "kos_rot_35" -> "Lambi Beach am Abend"
        "kos_rot_36" -> "Strand von Marmari mit Treibholz"
        "kos_rot_37" -> "Ebros-Therma-Strand"
        "kos_rot_38" -> "Kos aus der Luft"
        "kos_rot_39" -> "Strandschirme in Kardamena"
        else -> "Kos Impression"
    }
}
