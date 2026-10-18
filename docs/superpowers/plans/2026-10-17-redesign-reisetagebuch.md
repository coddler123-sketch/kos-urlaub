# Redesign „Reisetagebuch“ – Umsetzungsplan

Grundlage: `docs/superpowers/specs/2026-10-17-redesign-vorschlaege.md` (Abschnitt „Entscheidung“) und
Mockup `docs/mockups/kos-mockups.html`, Richtung A.

Festgelegt: hell und dunkel wählbar (System/Hell/Dunkel), keine vorgeschriebenen Tagesabläufe,
Tagebuchdaten **nur lokal** (SharedPreferences/DataStore, keine Cloud, kein Export in Version 1).
Inhalte aus `TripData.kt`, Tipps, Bus, SOS, Hotel bleiben unverändert. Wächter-Test
`NoRhodesLeftoversTest` und bestehende Unit-Tests bleiben grün.

Jede Etappe endet mit: `./gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:compileDebugAndroidTestKotlin -q`
ohne Ausgabe plus Emulator-Screenshot (Pixel_8, `com.example.kosurlaub.debug`) in hell und dunkel.

## Etappe 1 – Design-System und Themewahl
- `ui/theme`: neue Farbtokens (hell: Papier `#F6F0E4`, Tinte `#2B3A30`, Ägäis `#1F5F7A`, Alarm `#B8442F`;
  dunkel: warmes Anthrazit, gleiche Akzente), Formen, Abstände.
- Schriften: Fraunces (Überschriften) und Inter (Text) als Dateien in `res/font`, Lizenz (OFL) in `IMAGE_CREDITS.md`
  bzw. `FONT_CREDITS.md` vermerken.
- Einstellung `ThemeMode` (System/Hell/Dunkel) in `SettingsUi.kt` + Persistenz, Theme.kt liest sie.
- Prüfen: Alt-Farben (Teal, Purple*) entfernen, Kontrast AA in beiden Modi.

## Etappe 2 – Modus und Navigation
- `TripPhase` (VorReise / AufKos / Nachher) aus `TripData`, testbar mit fester Uhrzeit.
- Neue Hülle statt Bottom-Tabs: Tagesleiste (13.–28.09.2027) unten, oben Titel, SOS-Knopf immer sichtbar.
  Weitere Bereiche (Insel/Tipps, Bus, Hotel, Sprache, Einstellungen) über ein Menü, nicht als Tabs.
- Unit-Test: Phase und Tagesindex an Grenzen (Abflug, Ankunft Ortszeit Athen, Rückflug).

## Etappe 3 – Startseite (Vorfreude-Deckel)
- Tagesbild als Polaroid (bestehende 30 Bilder), Countdown groß in Serif, Flug-Chips,
  freie Checkliste (eigene Punkte, kein Vorgabetext außer optionalen Startpunkten zum Anhaken-oder-Löschen).
- Auf Kos wird daraus „Heute“ (Etappe 4).

## Etappe 4 – Tagesseite ohne Vorgaben
- Faktenkarten: Wetter/Wind/Meer (Open-Meteo, vorhanden), Sonnenauf-/untergang (rein berechnet), nächster Bus (Referenzfahrplan mit Stand).
- „Euer Tag“: leer, Einträge per „+ Notiz“, „+ Foto“ (Photo Picker, lokal kopiert), „+ Ort von der Merkliste“.
- „Ideen ansehen“: eingeklappt, öffnet die Tipp-Liste; nichts wird vorsortiert oder empfohlen.
- Test: Sonnenzeiten für Tigaki an zwei Daten gegen Referenzwerte, Tageseinträge speichern/laden.

## Etappe 5 – Tipps, Merkliste, Tagebuch
- Entdecken wird „Ideen und Orte“: Foto-Karten aus der bestehenden Tipp-Liste im neuen Look, Merkliste bleibt.
- Tagebuch: „Wollen wir noch“ (Merkliste) mit Abhaken, Datum, Foto, Satz; erledigte Einträge landen im Tagebuch.
- Rückblick „Unser Kos“ nach dem Rückflug: Anzahl Einträge, ein Foto je Tag, nur lokal.

## Etappe 6 – Vor Ort, Hotel, SOS, Sprache
- Bestehende Inhalte in den neuen Look übertragen, Inhalt unverändert. Hotel weiter mit „bestätigt/unsicher“.
- SOS bleibt unabhängig von Theme und Navigationsstand mit einem Tipp erreichbar.

## Etappe 7 – Widget und Abnahme
- Widget-Layout in Papier-Optik, folgt dem Systemmodus (hell/dunkel Ressourcen).
- Gesamtabnahme: beide Modi, beide Phasen (Testdatum per Debug-Einstellung), Screenshots, Wächter-Test.

## Reihenfolge und Risiken
Etappen 1 → 2 → 3 zuerst, danach 4 bis 6 in Reihenfolge, 7 zuletzt. Risiko: die Navigationsumstellung
in Etappe 2 berührt fast jede Seite; deshalb zuerst mit den alten Inhalten dahinter, dann Seite für Seite umziehen.
Nicht in Version 2: Teilen/Sync, Karte aus Richtung B (später als Seite „Insel“ möglich).

## Stand der Umsetzung (17.10.2026)

Etappen 1 bis 7 umgesetzt. Offen/bewusst anders als geplant: Tabs bleiben (Home, Ideen, Tagebuch, Vor Ort & SOS) plus Tagesleiste auf Home (der SOS-Knopf wurde auf Wunsch wieder entfernt); Widget folgt dem Systemmodus, nicht der App-Einstellung; Hintergrund-Abdunklung in der Galerie ist ohne Foto-Hintergrund wirkungslos; Rueckblick zeigt nur Zahlen.
