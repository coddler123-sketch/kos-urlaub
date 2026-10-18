# Kos Urlaub – Redesign-Vorschläge (Entwurf, nichts umgesetzt)

Stand: 17.10.2026. Ziel: Die App soll sich nicht mehr wie die Rhodos-App anfühlen.
Aufwand ist egal, Inhalte dürfen mitwachsen. Quellenregel aus Version 1 gilt weiter:
kein Inhalt ohne Quelle und Stand.

## Was heute an Rhodos erinnert

Dunkles Teal als Grundton, Kachel-Raster je Kategorie, drei Tabs (Home, Entdecken, Vor Ort & SOS),
Karten mit Foto-Verlauf, Kategorien-Denke („Tipps nach Thema“). Alles stammt aus dem Fork.

## Gemeinsames Grundprinzip für alle Richtungen

Heute ist die App nach **Themen** gegliedert. Neu: nach **Zeit und Situation**.
Vor der Reise zählt Vorfreude, auf Kos zählt „Was ist jetzt dran?“. Die App hat zwei Modi,
die sich automatisch aus `TripData` ergeben (vor Abflug / auf Kos / nach Rückflug).

## Drei Richtungen

### A – „Reisetagebuch“ (Papier, Wärme, persönlich) – Empfehlung

- **Look:** hell, warmes Papierweiß, Sand und Olivgrün, Akzent Ägäisblau. Serifen-Überschriften
  (Fraunces oder Playfair), Fließtext in einer ruhigen Sans. Fotos mit weißem Polaroid-Rand,
  leicht gekippt. Keine dunklen Karten.
- **Navigation:** statt Tabs eine Zeitleiste „15 Tage“ (13.–28.09.2027). Jeder Tag ist eine Seite
  mit Wetter, Sonnenauf-/untergang, Vorschlag, Bus-Hinweis. Unten nur Tagesleiste und ein
  Notfall-Knopf (immer erreichbar).
- **Vorfreude-Modus:** Startseite ist ein Tagebuchdeckel mit Foto, Countdown als handgesetzte Zahl,
  darunter „Noch zu erledigen“ (Packliste, Online-Check-in, Geld, Roaming).
- **Vor Ort:** Heute-Seite mit „Wetter jetzt“, „nächster Bus ab Tigaki“, „heute vorgeschlagen“.
- **Erinnerungen:** Merkliste wird zu „Wollen wir noch“, Abgehaktes wandert ins Tagebuch
  (Datum, Foto, ein Satz). Am Ende ein Rückblick „Unser Kos“.

### B – „Karte zuerst“ (Entdecker)

- **Look:** hell oder dunkel umschaltbar, kräftiger Akzent (Terrakotta), große Kartenfläche.
- **Navigation:** Vollbild-Karte der Insel (Osmdroid/OSM, Offline-Kacheln für Kos) mit Hotel als
  Anker, Tipps als Pins, Bus-Linie als Linie, Bottom-Sheet für Details und Fahrzeit ab Tigaki.
- **Inhalt:** Entfernungsringe ab Tigaki (10/20/30 Min), Filter „heute offen“, „Regentag“, „zu Fuß“.
- Stärke: räumliches Verständnis der Insel. Schwäche: Karte offline und Pflege der Pins.

### C – „Tagesmagazin“ (Editorial)

- **Look:** großflächige Fotos, viel Weißraum, fette Grotesk (z. B. Space Grotesk), Schwarz-Weiß
  plus ein Akzent (Kos-Blau). Wie ein Reisemagazin.
- **Navigation:** ein vertikaler Feed „Heute“ mit Karten in wechselnden Größen, oben eine
  Tages-Schnellleiste (Wetter, Bus, SOS). Tipps sind Artikel mit Lesezeit.
- Stärke: wirkt am meisten „anders“. Schwäche: mehr Text zu pflegen.

## Neue Inhalte (richtungsunabhängig, jeweils mit Quelle zu belegen)

1. **Tagesplan-Vorschläge** für die 15 Tage: Strandtage, Kos-Stadt, Asklepieion, Zia-Abend,
   Nisyros-Bootstour, Bodrum, Ruhetage. Wetter- und Wind-abhängig umsortierbar.
2. **Sonnenauf-/untergang und Golden Hour** pro Tag (rein berechnet, offline).
3. **Regentag-/Windtag-Alternativen** (Open-Meteo ist schon angebunden).
4. **Offline-Fahrplan-Hinweis** mit Stand-Datum prominent (Referenz KTEL gültig ab 28.09.2026).
5. **Taverne-Journal:** Orte eintragen, bewerten, Foto, was bestellt wurde.
6. **Packliste und Reise-Checkliste** (Pass/Perso, Condor-Check-in, Medikamente, Adapter, Sonnenschutz),
   Gepäck-Freigrenzen aus `TripData` eingebunden (23 kg / 8 kg).
7. **Flug-Tag-Karten:** Abflug- und Rückflugtag als eigener Ablauf (Abholzeit, Check-in, Zeitzonen-Hinweis +1 h).
8. **Griechisch im Alltag:** Sprachführer mit Audio (Android-TTS), Favoriten-Sätze.
9. **Hotel-Seite** mit klarer Trennung „bestätigt“ und „unsicher“ (Zimmerzahl, Haltestelle, Safe)
   und Fragenliste für die Rezeption beim Check-in.
10. **Rückblick** nach der Reise: Statistik (Strandtage, besuchte Orte, Tavernen), ein Foto je Tag.

## Technische Folgen

- Compose bleibt. Neues Design-System (Farben, Typo, Formen) ersetzt `ui/theme`; Tipp- und
  Reisedaten bleiben (`TripData.kt`, Tipp-Listen, Busdaten).
- Moduswechsel Vorfreude/Vor Ort über `TripData`-Daten, testbar mit festem Datum.
- Widget bekommt denselben Look (eigenes Layout in `res/layout`).
- A: kein neues SDK nötig. B: Karten-Bibliothek plus Offline-Kacheln. C: Schriftdateien einbinden.
- Der Wächter-Test gegen Rhodes-Reste und die bestehenden Unit-Tests bleiben als Sicherheitsnetz.

## Entscheidungen, die ich brauche

1. Richtung A, B, C oder Mischung (z. B. A-Look mit Karte aus B als Seite „Insel“).
2. Hell, dunkel oder automatisch.
3. Welche der neuen Inhalte (1–10) sollen in die erste neue Version?
4. Soll ich vor der Umsetzung für die gewählte Richtung zwei, drei Mockups als HTML-Bilder erstellen?

## Entscheidung (17.10.2026)

Richtung **A – Reisetagebuch**, mit diesen Vorgaben vom Nutzer:

- **Keine Vorschriften.** Die App schlägt keinen Tagesablauf vor. Tagesseiten zeigen nur Fakten
  (Wetter, Wind, Sonne, nächster Bus) und gehören den Nutzern: eigene Notizen, Fotos, Orte aus
  der Merkliste. Ideen und Tipps gibt es nur eingeklappt oder auf Abruf. Auch die Checkliste ist
  leer bzw. frei editierbar. Neue Inhalte 1 (Tagesplan-Vorschläge) und 3 (Regentag-Alternativen)
  entfallen als Vorgabe und werden höchstens zu „Ideen ansehen“.
- **Hell und Dunkel auswählbar** (System / Hell / Dunkel in den Einstellungen), Standard: System.
  Dunkelvariante: dunkles Papier (warmes Anthrazit statt Teal), gleiche Serifen und Akzente.
- Überarbeitetes Mockup A liegt in `docs/mockups/kos-mockups.html`.
