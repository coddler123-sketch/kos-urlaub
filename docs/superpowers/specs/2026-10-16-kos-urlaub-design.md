# Kos Urlaub – Design (Version 1)

Stand: 16.10.2026. Private Android-App für zwei Personen, kein Store.

## Hintergrund

Die bisherige App (`kos-countdown-widget`) war eine Rhodos-App, die per Suchen-und-Ersetzen
zur Kos-App wurde. Folge: Rhodos-Fahrpläne, Rhodos-Foto-Spots, Rhodos-Nachrichtenquellen,
Rhodos-Telefonnummern (Vorwahl 22410) und bis jetzt auch Rhodos-Wetterkoordinaten.
Das alte Projekt bleibt unverändert als Vorlage zum Kopieren. Die neue App entsteht in
`C:\Claude_Code_Projects\kos-urlaub`, Paket `com.example.kosurlaub`.

## Ziel

Beides gleich wichtig: Vorfreude vor der Reise (Countdown, Widget, Bilder) und Hilfe vor Ort
(Hotel, Bus, Tipps, SOS, Wetter).

## Umfang Version 1

| Tab | Inhalt |
|---|---|
| Home | Countdown bis zum Abflug, Widget, Tagesbild aus 30 Bildern, Tagesfakt |
| Entdecken | 14 recherchierte Tipps mit Merkliste, Foto-Spots (nur Kos) |
| Vor Ort | Hotel Ilios K. Village, Bus KTEL Kos, Sprachführer, Tavernen-Rechner, SOS, Wetter und Meer Tigaki |

Nicht in Version 1: Kos-News (später, braucht Server), Update-Check über GitHub.

## Reisedaten (einzige Quelle: `TripData.kt`)

- Hinflug Mo 13.09.2027, Hamburg 13:25 → Kos 17:45 (Ortszeiten), Condor DE1640.
- Rückflug Di 28.09.2027, Kos 11:35 → Hamburg 14:00, Condor DE1641.
- Freigepäck je Person: 23 kg aufgegeben, 8 kg Handgepäck (max. 40 × 55 × 20 cm).
- Veranstalter ByeBye, Doppelzimmer mit Klimaanlage, Bad oder Dusche, Balkon oder Terrasse, All Inclusive.
- Zeitzonen: Abflug `Europe/Berlin`, Ankunft `Europe/Athens` (Kos ist 1 Stunde voraus).
- Hotel: ILIOS.K Village Resort, Tigaki, 85300 Kos, Koordinaten 36,8800 / 27,1834.

## Hotel-Daten und Abweichungen

Quellen: vtours, Corendon, Hotelmix, top-hotels-gr. Gesicherte Angaben: 4 Sterne, All Inclusive als Buffet,
Check-in ab 15:00, Check-out bis 11:00, Sandstrand und Ortszentrum ca. 0,9–1,1 km, deutschsprachig geführt,
Koordinaten stimmen in allen Quellen überein. Widersprüche (in der App als „unsicher“ kennzeichnen):
Zimmerzahl 130/151, Bushaltestelle ≤ 50 m/> 400 m, Parkplatz gebührenpflichtig/kostenlos, Zimmersafe inklusive/gegen Gebühr.
Offen: Straße (keine Quelle nennt eine), Telefon (nur top-hotels-gr: +30 22420 69411, nicht gegengeprüft; Vorwahl 22420 passt zu Kos).

## Technik

- Kotlin, Jetpack Compose, ein Modul, gleiche Architektur wie die alte App.
- Widget als klassisches Widget mit Layout-Datei.
- Wetter über Open-Meteo, Koordinaten aus `TripData.kt`.
- Bilder: 30 Pexels-Bilder für die Rotation plus Wikimedia-Bilder für Ausflüge.
  Nur privat genutzt, Quellenliste in `IMAGE_CREDITS.md`.

## Qualitätsregeln

- Kein Inhalt ohne Quelle und Stand. Was nicht belegt werden kann, kommt nicht in die App.
- Busfahrplan: echte Zeiten von KTEL Kos (ktel-kos.gr) mit Quelle und Stand, sonst Hinweis „nicht verifiziert“.
- SOS: 112, 100 (Polizei), 166 (Rettung), 199 (Feuerwehr), Hotel-Rezeption sobald Nummer bestätigt ist.
- Tests: Countdown mit Zeitzonen, Tipp-Liste, Reisedaten. Vor jeder Abnahme Sichtprüfung auf dem Emulator.

## Aus der alten App übernehmen

Countdown-Rechner mit Zeitzonen und Tests, Bilderrotation, Widget, Theme, Wetter, die 14 Tipps (mit Maps-CSV),
Sprachführer, Tavernen-Rechner, Bilder.

## Nicht übernehmen

Fahrplan- und Transit-System (KTEL Rodou, Lindos, Kolymbia, Faliraki), News und Worker, alte Foto-Spots,
alte Strings, SPEC.md, README, `site/`, `rhodos-release-key.jks`.

## Neu zu schreiben

- Foto-Spots mit echten Kos-Orten (z. B. Kastri-Insel, Burg Neratzia, Paradise Beach, Zia, Asklepieion, Alikes-Salzsee), jeweils mit Beste-Zeit.
- Tagesfakten über Kos (mit Quelle prüfen).
- Bus-Fahrplan Tigaki ↔ Kos-Stadt aus echter Quelle.
- Reise-Seite mit Flug- und Gepäckdaten.

## Änderungen Version 2 (17.10.2026, Reisetagebuch-Redesign)

Dieser Abschnitt ersetzt die Tabelle „Umfang Version 1“ und die Technik-Angaben zu Optik und Navigation.
Details: `2026-10-17-redesign-vorschlaege.md` und `docs/superpowers/plans/2026-10-17-redesign-reisetagebuch.md`.

| Tab | Inhalt |
|---|---|
| Home | Tagesleiste (13.–28.09.2027), Polaroid-Tagesbild, Countdown, freie Checkliste, Schnellzugriff; ein Tipp auf einen Tag öffnet die Tagesseite |
| Ideen | 14 recherchierte Tipps („Ideen & Orte“) mit Merkliste und Foto-Spots |
| Tagebuch | „Wollen wir noch“, Erlebt (mit Datum), Einträge je Tag, Rückblick |
| Vor Ort & SOS | Reise & Meer, Bus & Mobil, Ausflüge & Sprache, SOS & Hilfe (auch über die Kachel „SOS Notfall“ auf Home) |

- Optik: Hell, Dunkel oder System (Einstellung in der App), Schriften Fraunces und Inter, Farb-Tokens in `ui/theme`.
- Keine Vorgaben: Tagesseiten zeigen nur Fakten (Sonne berechnet, Wetter, Bus-Referenz) und „Euer Tag“ gehört den Nutzern; Daten nur lokal.
- Entfernt: Tagesvorschläge auf Home, Abdunklung des Hintergrunds, ungenutzte Fahrplan-/PDF-Texte.
- Quellenprüfung: Ausflugs- und Fährlinks zeigen auf Kos-Quellen (Wikipedia, 12ne.gr für Kos–Nisyros, bodrumexpresslines.com, kos.gr). Rhodos-Altlasten (sebeco.gr, seadreams.gr, hh.gr, Palace of the Grand Master) sind entfernt.
- Karten-Links in `Google-Maps-Links.csv` sind Ortsnamen-Suchen, keine geprüften Koordinaten.
- Debug-Build: Einstellungen enthalten ein Testdatum (Vor Abflug / Auf Kos / Nach Rückflug); im Release-Build nicht vorhanden.
- Nach dem Rückflug (28.09.2027 11:35) zeigt Home „Schön war's!“ statt Countdown; der Tagebuch-Rückblick heißt dann „Unser Kos“.
