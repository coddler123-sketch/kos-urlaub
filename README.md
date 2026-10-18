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

## Updates

Die App prüft beim Start die neueste GitHub-Release dieses Repos und bietet die Installation an (Einstellungen → Version → Prüfen).

Neue Version veröffentlichen:

1. `versionCode` und `versionName` in `app/build.gradle.kts` erhöhen.
2. `.\gradlew.bat assembleRelease` (braucht `keystore.properties` und `kos-release-key.jks`, beide nicht im Repo).
3. APK als `Kos-Urlaub-<version>.apk` an ein Release mit Tag `v<version>` hängen.

Der Release-Key muss immer derselbe sein, sonst lässt sich ein Update nicht installieren.
