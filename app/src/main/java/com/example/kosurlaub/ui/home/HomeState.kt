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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

private fun shortWeekday(dateIso: String): String = dateIso.toShortGermanWeekday()

data class HomeState(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val isReached: Boolean,
    val isOnVacation: Boolean,
    val isOver: Boolean,
    val phrase: String,
    val weather: WeatherSnapshot?,
    val weatherStatus: String,
    val backgroundRes: Int,
    val departureDate: String,
    val departureTime: String,
    val progress: Float,
    val sunsetLabel: String?,
    val sunriseLabel: String?,
    val factOfTheDay: String
) {
    companion object {
        fun load(context: Context): HomeState {
            val remaining = CountdownCalculator.calculate()

            val phrase = phraseOfTheDay(
                context.resources.getStringArray(R.array.widget_phrases),
                context.resources.getStringArray(R.array.widget_phrases_daily),
                CountdownCalculator.daysUntilDeparture(),
                localEpochDay()
            )

            val weather = WeatherRepository.cached(context)?.let { w ->
                WeatherSnapshot(
                    temperatureLabel = w.temperatureLabel,
                    apparentTemperatureLabel = w.apparentTemperatureLabel,
                    humidityLabel = w.humidityLabel,
                    precipitationLabel = w.precipitationLabel,
                    windSpeedLabel = w.windSpeedLabel,
                    iconRes = WeatherRepository.iconFor(w),
                    forecastDays = w.forecast.take(7).map { day ->
                        ForecastEntry(
                            weekday = shortWeekday(day.dateIso),
                            iconRes = WeatherRepository.iconForCode(day.weatherCode),
                            minTemp = day.minTemperatureCelsius,
                            maxTemp = day.maxTemperatureCelsius
                        )
                    },
                    spokenReport = WeatherReportFormatter.spokenReport(w)
                )
            }
            val weatherStatus = weatherStatus(context, weather)
            val backgroundRes = Images.resourceOfTheDay(context)
            val berlin = java.util.TimeZone.getTimeZone(TripData.outbound.fromZone)
            val df = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).apply { timeZone = berlin }
            val tf = SimpleDateFormat("HH:mm", Locale.GERMAN).apply { timeZone = berlin }
            val target = CountdownCalculator.departureCalendar()
            val rawWeather = WeatherRepository.cached(context)
            val sunsetLabel = rawWeather?.sunsetIso?.let { parseSolarEventLabel(it) }
            val sunriseLabel = rawWeather?.sunriseIso?.let { parseSolarEventLabel(it) }
            return HomeState(
                days = remaining.days, hours = remaining.hours,
                minutes = remaining.minutes, seconds = remaining.seconds,
                isReached = remaining.isReached,
                isOnVacation = remaining.isOnVacation,
                isOver = remaining.isOver,
                phrase = phrase,
                weather = weather,
                weatherStatus = weatherStatus,
                backgroundRes = backgroundRes,
                departureDate = df.format(target.time),
                departureTime = tf.format(target.time),
                progress = CountdownCalculator.progressFraction(),
                sunsetLabel = sunsetLabel,
                sunriseLabel = sunriseLabel,
                factOfTheDay = kosFactOfTheDay()
            )
        }

        private fun parseSolarEventLabel(sunsetIso: String): String? {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("Europe/Athens")
                }
                val sunsetTime = sdf.parse(sunsetIso) ?: return null
                val now = System.currentTimeMillis()
                val timeLabel = SimpleDateFormat("HH:mm", Locale.GERMAN).apply {
                    timeZone = java.util.TimeZone.getTimeZone("Europe/Athens")
                }.format(sunsetTime)
                val diffMillis = sunsetTime.time - now
                if (diffMillis > 0) {
                    val h = TimeUnit.MILLISECONDS.toHours(diffMillis)
                    val m = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60
                    if (h > 0) "$timeLabel Uhr · noch ${h} Std. ${m} Min."
                    else "$timeLabel Uhr · noch ${m} Min."
                } else {
                    "$timeLabel Uhr"
                }
            } catch (e: Exception) { null }
        }

        private fun weatherStatus(context: android.content.Context, weather: WeatherSnapshot?): String {
            if (weather == null) return "Noch nicht geladen"
            val lastFetch = WeatherRepository.lastFetchMillis(context)
            if (lastFetch <= 0L) return "Gespeichert"

            val ageMillis = (System.currentTimeMillis() - lastFetch).coerceAtLeast(0L)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(ageMillis)
            if (minutes < 1) return "Gerade aktualisiert"
            if (minutes < 60) return "Vor $minutes Min. aktualisiert"

            val hours = TimeUnit.MILLISECONDS.toHours(ageMillis)
            return "Vor $hours Std. aktualisiert"
        }
    }
}

private fun kosFactOfTheDay(): String {
    val facts = listOf(
        // Geografie & Natur
        "Kos ist mit knapp 300 km² die drittgrößte Insel der Dodekanes-Inselgruppe.",
        "Kos liegt nur wenige Kilometer vor der türkischen Küste – bei klarer Sicht sieht man das türkische Festland.",
        "Die Insel hat viele Sonnentage im Jahr – sie gehört zu den sonnenreichen Inseln der Ägäis.",
        "Kos gilt als eine der fahrradfreundlichsten Inseln Griechenlands – viele Wege sind flach und gut ausgebaut.",
        "Der höchste Punkt der Insel ist der Berg Dikeos mit rund 846 m – von dort reicht der Blick über die ganze Ägäis.",
        "Tigaki liegt an der Nordküste, westlich von Kos-Stadt, direkt an einem langen Sandstrand.",
        "Gleich neben Tigaki liegt ein Salzsee-Naturschutzgebiet – im Frühjahr und Herbst ein beliebter Ort für Zugvögel.",
        "Im September ist das Meer rund um Kos meist noch angenehm warm.",
        // Hippokrates & Asklepieion
        "Kos gilt als Geburtsort von Hippokrates, dem \"Vater der modernen Medizin\" – vermutlich um 460 v. Chr.",
        "Der Hippokrates-Eid, den junge Ärzte traditionell ablegen, geht auf seine Lehren zurück.",
        "In Kos-Stadt steht die berühmte Platane des Hippokrates – der Legende nach unterrichtete er dort seine Schüler.",
        "Das Asklepieion, rund 4 km von Kos-Stadt entfernt, war ein antikes Heiligtum und eine Art frühes Krankenhaus.",
        "Die Anlage des Asklepieion verteilt sich über drei Terrassen, verbunden durch breite Marmortreppen.",
        "Ausgegraben wurde das Asklepieion erst Anfang des 20. Jahrhunderts von einem deutschen Archäologen.",
        // Geschichte
        "Ab dem frühen 14. Jahrhundert wurde Kos vom Johanniter-Ritterorden kontrolliert, der dort die Burg Nerantzia errichtete.",
        "In den 1520er-Jahren eroberten die Osmanen die Dodekanes; die Johanniter zogen später nach Malta weiter.",
        "Von 1912 bis 1943 gehörte Kos wie die übrigen Dodekanes-Inseln zu Italien.",
        "Ein schweres Erdbeben 1933 zerstörte große Teile von Kos-Stadt, die danach von den Italienern neu gestaltet wurde.",
        "Erst 1948 wurden die Dodekanes-Inseln offiziell Teil Griechenlands.",
        "Die Festung Antimachia in der Inselmitte wurde ebenfalls vom Johanniterorden erbaut und sicherte die Küste.",
        // Natur, Ausflüge & Kulinarik
        "Zia, ein Bergdorf am Fuß des Dikeos, ist berühmt für seinen Blick auf den Sonnenuntergang über der Ägäis.",
        "Die Therme Embros bei Agios Fokas ist eine natürlich warme Quelle direkt am Meer.",
        "Von Kos aus starten Tagesausflüge mit dem Boot zur Vulkaninsel Nisyros und zur türkischen Stadt Bodrum.",
        "\"Tzatziki\" aus Joghurt, Gurke und Knoblauch gehört in Griechenland zu fast jedem Tavernen-Tisch.",
        "\"Melekouni\" ist ein Sesam-Honig-Riegel und ein beliebtes griechisches Mitbringsel.",
        "Im September ist Kos besonders angenehm: weniger Touristen, noch warmes Meer, erstes Herbstlicht."
    )
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return facts[dayOfYear % facts.size]
}
