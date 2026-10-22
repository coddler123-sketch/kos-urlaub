package com.example.kosurlaub.ui.weather

/** Strandwahl aus Windstärke und -richtung. Tigaki liegt an der Nordküste, also dem Nordwind (Meltemi) zugewandt. */
object BeachAdvice {
    private val compass = listOf("Nord", "Nordost", "Ost", "Südost", "Süd", "Südwest", "West", "Nordwest")

    /** Richtung, aus der der Wind weht. */
    fun directionName(degrees: Int): String = compass[(((degrees % 360) + 360) % 360 + 22) / 45 % 8]

    private fun fromNorth(degrees: Int): Boolean {
        val d = ((degrees % 360) + 360) % 360
        return d >= 315 || d <= 45
    }

    fun advice(speedKmh: Int, directionDegrees: Int?): String = when {
        speedKmh < 15 -> "Kaum Wind. Alle Strände gehen, Tigaki und Marmari sind ideal."
        directionDegrees == null ->
            if (speedKmh >= 25) "Es weht kräftig. Windschutz mitnehmen, Südstrände sind bei Nordwind oft ruhiger."
            else "Mäßiger Wind. Tigaki geht gut."
        fromNorth(directionDegrees) && speedKmh >= 20 ->
            "Nordwind (Meltemi): Auf der Nordküste ist es wellig. Südstrände wie Kardamena oder Kefalos sind oft ruhiger, dorthin braucht ihr Auto oder Taxi."
        fromNorth(directionDegrees) -> "Leichter Nordwind. Tigaki geht noch gut, nachmittags kann es auffrischen."
        else -> "Wind aus ${directionName(directionDegrees)}. Die Nordküste ist meist geschützt, Tigaki ist ein guter Platz."
    }
}
