# ToDo

## App-Umbau: Phasen-App im Postkarten-Look (Start: 2026-10-19)

Entwurf: [docs/superpowers/specs/2026-10-18-app-konzept-design.md](superpowers/specs/2026-10-18-app-konzept-design.md)

Reihenfolge, jedes Teilprojekt einzeln lauffähig und releasebar:

- [x] **1. Postkarten-Designsystem** – Farben, Schriften (Serife Titel, Sans Zahlen), Foto-Karte, Stempel-Chip, Papier-Panel, Perforationskante; hell und dunkel. Ändert noch keine Funktion. Vorgabe aus dem Mockup-Review: keine Schrift unter 11sp, Innenabstand in Karten und Chips mindestens 8dp (Mockup `docs/mockups/app-konzept.html` nutzt noch 9–10px).
- [ ] **2. Navigation und Phasen-Startseite** – Tabs Heute · Entdecken · Tagebuch; Startseite je nach `TripPhase` (vorher / auf Kos / danach).
- [ ] **3. Entdecken** – Kompass und Reise zu einer Seite mit Kategorien; gemerkte Tipps einem Tag zuordnen (Grundlage für den Tagesplan).
- [ ] **4. Tagebuch und Rückblick** – Tagebuch im Postkarten-Look, Rückblick am Ende der Reise.

## Offen aus den Widgets

- [ ] Abflugtafel: Flugzeug-Marker auf dem Fortschrittsbalken (braucht dynamische Position, mit RemoteViews nur über Bitmap oder Tricks)
- [ ] Widget-Stile auf dem echten Handy prüfen (Auswahl beim Hinzufügen, Lesbarkeit)
