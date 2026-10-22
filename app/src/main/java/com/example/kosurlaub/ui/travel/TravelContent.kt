package com.example.kosurlaub.ui.travel
import com.example.kosurlaub.*

import com.example.kosurlaub.R
import com.example.kosurlaub.MainActivity
import com.example.kosurlaub.ui.home.*
import com.example.kosurlaub.ui.compass.*
import com.example.kosurlaub.ui.travel.*
import com.example.kosurlaub.ui.weather.*
import com.example.kosurlaub.ui.settings.*
import com.example.kosurlaub.ui.theme.*
import com.example.kosurlaub.widget.*

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.net.URI

internal data class TravelSource(
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    val url: String
)

internal data class EmergencyContact(
    val number: String,
    @param:StringRes val titleRes: Int
)

internal data class TravelChecklistItem(
    val id: String,
    @param:StringRes val titleRes: Int
)

internal val travelSources = listOf(
    TravelSource(
        titleRes = R.string.travel_kos_gr_title,
        descriptionRes = R.string.travel_kos_gr_description,
        url = "https://www.kos.gr/"
    )
)

internal val ferryAndEventSources = listOf(
    TravelSource(
        titleRes = R.string.travel_dodekanisos_title,
        descriptionRes = R.string.travel_dodekanisos_description,
        url = "https://www.12ne.gr/"
    ),
    TravelSource(
        titleRes = R.string.travel_bodrum_title,
        descriptionRes = R.string.travel_bodrum_description,
        url = "https://www.bodrumexpresslines.com/"
    ),
    TravelSource(
        titleRes = R.string.travel_events_title,
        descriptionRes = R.string.travel_events_description,
        url = "https://www.kos.gr/"
    )
)

internal val emergencyContacts = listOf(
    EmergencyContact("100", R.string.travel_police),
    EmergencyContact("166", R.string.travel_ambulance),
    EmergencyContact("199", R.string.travel_fire),
    EmergencyContact("108", R.string.travel_coast_guard)
)

internal val travelChecklist = listOf(
    TravelChecklistItem("documents", R.string.travel_check_documents),
    TravelChecklistItem("insurance", R.string.travel_check_insurance),
    TravelChecklistItem("booking", R.string.travel_check_booking),
    TravelChecklistItem("driving", R.string.travel_check_driving),
    TravelChecklistItem("sun", R.string.travel_check_sun),
    TravelChecklistItem("charger", R.string.travel_check_charger),
    TravelChecklistItem("medicine", R.string.travel_check_medicine)
)

internal fun isTrustedTravelUrl(url: String): Boolean {
    val uri = runCatching { URI(url) }.getOrNull() ?: return false
    val host = uri.host?.lowercase() ?: return false
    return uri.scheme == "https" && host in TRUSTED_TRAVEL_HOSTS
}

private val TRUSTED_TRAVEL_HOSTS = setOf(
    "www.kos.gr",
    "de.wikipedia.org",
    "www.12ne.gr",
    "www.bodrumexpresslines.com"
)

internal data class GreekPhrase(
    val category: String,
    val greek: String,
    val phonetic: String,
    val german: String,
    val ttsText: String = greek
)

internal val greekPhrases = listOf(
    GreekPhrase("Taverne & Bestellung", "Ton logariasmó, parakaló", "Ton lo-ga-rjas-mó, pa-ra-ka-ló", "Die Rechnung bitte", "Τον λογαριασμό, παρακαλώ"),
    GreekPhrase("Taverne & Bestellung", "Stin ygía mas! / Yamas!", "Stin i-jí-a mas / Ja-mas", "Zum Wohl / Prost!", "Στην υγεία μας! Γειά μας!"),
    GreekPhrase("Taverne & Bestellung", "Éna neró, parakaló", "É-na ne-ró, pa-ra-ka-ló", "Ein Wasser bitte", "Ένα νερό, παρακαλώ"),
    GreekPhrase("Taverne & Bestellung", "Polý nóstimo!", "Po-lí nós-ti-mo", "Sehr lecker!", "Πολύ νόστιμο!"),
    GreekPhrase("Taverne & Bestellung", "Dyo biras, parakaló", "Di-o bí-ras, pa-ra-ka-ló", "Zwei Bier bitte", "Δύο μπίρες, παρακαλώ"),
    GreekPhrase("Taverne & Bestellung", "Éna oúzo, parakaló", "É-na u-zo, pa-ra-ka-ló", "Einen Ouzo bitte", "Ένα ούζο, παρακαλώ"),
    GreekPhrase("Taverne & Bestellung", "To fayitó ítan katapliktikó!", "To fa-ji-tó í-tan ka-ta-plik-ti-kó", "Das Essen war fantastisch!", "Το φαγητό ήταν καταπληκτικό!"),
    GreekPhrase("Begrüßung & Danke", "Kaliméra", "Ka-li-mé-ra", "Guten Morgen / Guten Tag", "Καλημέρα"),
    GreekPhrase("Begrüßung & Danke", "Kalispera", "Ka-lis-pé-ra", "Guten Abend", "Καλησπέρα"),
    GreekPhrase("Begrüßung & Danke", "Efcharistó polý", "Ef-cha-ris-tó po-lí", "Vielen Dank", "Ευχαριστώ πολύ"),
    GreekPhrase("Begrüßung & Danke", "Parakaló", "Pa-ra-ka-ló", "Bitte / Gern geschehen", "Παρακαλώ"),
    GreekPhrase("Begrüßung & Danke", "Yássas / Yássou", "Já-sas / Já-su", "Hallo / Tschüss (Formell / Informell)", "Γειά σας! Γειά σου!"),
    GreekPhrase("Begrüßung & Danke", "Miláte germaniká?", "Mi-lá-te ger-ma-ni-ká", "Sprechen Sie Deutsch?", "Μιλάτε γερμανικά;"),
    GreekPhrase("Unterwegs & Fragen", "Poú eínai i paralía?", "Pu í-ne i pa-ra-lí-a?", "Wo ist der Strand?", "Πού είναι η παραλία;"),
    GreekPhrase("Unterwegs & Fragen", "Poú eínai to stási?", "Pu í-ne to stá-si", "Wo ist die Bushaltestelle?", "Πού είναι η στάση;"),
    GreekPhrase("Unterwegs & Fragen", "Nai / Óchi", "Nä / Ó-chi", "Ja / Nein (Vorsicht: 'Nai' heißt Ja!)", "Ναι, Όχι"),
    GreekPhrase("Unterwegs & Fragen", "Gatáki", "Ga-tá-ki", "Kätzchen (für die süßen Tavernenkatzen)", "Γατάκι"),
    GreekPhrase("Unterwegs & Fragen", "Voítheia!", "Vo-í-thi-a", "Hilfe! (Notfall)", "Βοήθεια!")
)
