# Kos Urlaub

Private Android-App für den Kos-Urlaub (13.–28.09.2027): Countdown, Widget, Bilder des Tages,
Hotel- und Reiseinfos, Tipps, Wetter, Sprachführer, Tavernen-Rechner, SOS und Bus.

## Bauen und starten

    ./gradlew.bat :app:assembleDebug
    ./gradlew.bat :app:testDebugUnitTest

Debug-Paket: `com.example.kosurlaub.debug`. Reisedaten stehen nur in `TripData.kt`.

## Regeln

- Kein Inhalt ohne Quelle und Stand (Bus, Hotel, Tipps).
- Der Wächter-Test im Unit-Test-Ordner schlägt fehl, wenn Altlasten aus dem Vorgängerprojekt im Quelltext auftauchen.
