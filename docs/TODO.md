# ToDo

## App-Umbau: Phasen-App im Postkarten-Look (Start: 2026-10-19)

Entwurf: [docs/superpowers/specs/2026-10-18-app-konzept-design.md](superpowers/specs/2026-10-18-app-konzept-design.md)

Reihenfolge, jedes Teilprojekt einzeln lauffähig und releasebar:

- [x] **1. Postkarten-Designsystem** – Farben, Schriften (Serife Titel, Sans Zahlen), Foto-Karte, Stempel-Chip, Papier-Panel, Perforationskante; hell und dunkel. Ändert noch keine Funktion. Vorgabe aus dem Mockup-Review: keine Schrift unter 11sp, Innenabstand in Karten und Chips mindestens 8dp (Mockup `docs/mockups/app-konzept.html` nutzt noch 9–10px).
- [x] **2. Navigation und Phasen-Startseite** – Tabs Heute · Guide; Startseite je nach `TripPhase` (vorher / auf Kos / danach).
- [x] **3. Guide** – Kompass und Reise zu einer Seite mit Kategorien; gemerkte Tipps einem Tag zuordnen (Grundlage für den Tagesplan).
- [x] **4. Rückblick** – Tagebuch und Tagesplan sind entfallen (Entscheidung 2026-10-21); der Rückblick am Ende der Reise bleibt.

## Offen aus den Widgets

- [ ] Abflugtafel: Flugzeug-Marker auf dem Fortschrittsbalken (braucht dynamische Position, mit RemoteViews nur über Bitmap oder Tricks)
- [ ] Widget-Stile auf dem echten Handy prüfen (Auswahl beim Hinzufügen, Lesbarkeit)

## Offen aus Teilprojekt 2

- [x] Schnellzugriff-Karten auf Heute (auf Kos): geprüft am 22.10.2026 auf dem Xiaomi, sie scrollen nur unter der Tagesleiste durch, am Ende nichts verdeckt
- [x] SOS-Knopf: ist inzwischen eine Kachel im Schnellzugriff, kein schwebender Knopf mehr
- [x] Meer-Temperatur: lädt beim Anzeigen von Heute selbst (geprüft)
- [ ] Instrumentierte Tests komplett auf dem Emulator laufen lassen (nur der Merken-Test wurde einzeln geprüft)

## Befund Guide-Filter (2026-10-19, Teil von Teilprojekt 3)

Erledigt: Quick-Filter gruppiert im Filter-Sheet (Ort, Anfahrt, Dauer, Stimmung), Tipps als Foto-Raster, Umschalter Tipps | Reise in Postkarten-Farben. Offen bleibt die Reise-Seite in Teilprojekt 3.

- Filter kommen dreifach vor: Kategorie-Chips (Alle, Tigaki, Essen, Strände, …), darunter 12 "Quick Filter" (Ort, Anfahrt, Dauer, Stimmung gemischt) und weiter unten dieselben Kategorien noch einmal als Foto-Karten.
- Doppelungen: "Tigaki" und "Essen" sind Kategorie und Quick Filter; "Rund ums Hotel" ≈ "Tigaki"; "Ohne Auto" ≈ "Zu Fuß" + "Bus"; "Tagesausflug" ≈ "Halber Tag".
- Von 12 Quick-Filtern sind nur 3–4 ohne Wischen sichtbar; die Chips sind 10sp klein (Regel: mindestens 11sp) und mit Emojis gemischt.
- Der Umschalter Tipps | Reise nutzt die Standard-Lavendelfarbe von Material 3 statt der Postkarten-Palette.
- Kopf: "Überrascht uns!", "20 ausgewählte Tipps", Titel "Ideen & Orte", Hero-Karte und Suchfeld stapeln sich über dem Inhalt.
- Vorschlag: eine Chip-Zeile (Kategorien), ein Knopf "Filter" mit gruppiertem Sheet (Ort, Anfahrt, Dauer, Stimmung), Tipps als Foto-Karten im 2-Spalten-Raster wie im Mockup, Umschalter in Postkarten-Farben.
