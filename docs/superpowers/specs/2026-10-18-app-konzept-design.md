# App-Konzept: Phasen-App im Postkarten-Look

Stand: 2026-10-18, Entwurf, mit dem Nutzer abgestimmt. Noch kein Code.

## Problem

Die App zeigt in jeder Reisephase dasselbe, verteilt es auf vier Tabs (Home, Kompass, Tagebuch, Reise) und wirkt eher wie ein Werkzeug als wie ein Urlaub. Gewählte Hauptprobleme: zu viele Tabs und Inhalte, kein Urlaubsgefühl, keine Anpassung an die Reisephase.

## Ziel

Eine App, deren Startseite sich je nach Phase (vorher, auf Kos, danach) ändert, mit weniger Tabs und einer durchgehenden Postkarten-Optik wie in den Widgets.

## Entscheidungen

- Konzept 1 (Phasen-App) mit der Optik aus Konzept 2 (Postkarte), durchgehend in der ganzen App.
- Tabs: **Heute · Entdecken · Tagebuch**.
- „Heute“ auf Kos zeigt von oben nach unten: Wetter und Meer, Tagesplan, nächster Bus, Tipp des Tages. SOS bleibt als fester Knopf erreichbar.
- Verworfen: Reise als Zeitleiste (Konzept 3), wegen neuem Datenmodell und hohem Aufwand.

## Teilprojekte

Jedes Teilprojekt wird einzeln gebaut, getestet und released.

1. **Postkarten-Designsystem.** Gemeinsame Bausteine: Farben, Schriften (Serife für Titel, Sans für Zahlen), Foto-Karte, Stempel-Chip, Papier-Panel, Perforationskante. Hell und dunkel bleiben wählbar. Keine Funktionsänderung.
2. **Navigation und Phasen-Startseite.** Neue Tabs. Startseite je nach `TripPhase`:
   - Vorher: Abflugtafel-Countdown als Held, Flug und Hotel, gemerkte Tipps, Wetter-Trend.
   - Auf Kos: siehe Entscheidungen.
   - Danach: Rückblick mit Zahlen und Fotos.
3. **Entdecken.** Kompass (Tipps, gemerkt/besucht) und Reise (Hotel, Bus, Sprachführer, Fotospots, Notfall) werden eine Seite mit Kategorien. Gemerkte Tipps lassen sich einem Tag zuordnen, daraus entsteht der Tagesplan.
4. **Tagebuch und Rückblick.** Tagebuch im Postkarten-Look, Rückblick am Ende der Reise.

## Offene Fragen für den Start (Teilprojekt 2: entschieden, siehe Plan 2026-10-19-navigation-and-phase-home.md)

- Woher kommen Tagesplan-Einträge genau: nur gemerkte Tipps, oder auch frei eingetragene Punkte?
- Tipp des Tages: nach Wetter und Uhrzeit gefiltert oder zufällig aus den gemerkten?
- Wie viel vom Rückblick (Zahlen, Fotos) ist im ersten Wurf nötig?

## Bestehender Code, auf dem aufgebaut wird

`MainActivity.kt` (`MainDestination`), `ui/home/TripPhase.kt`, `HomeState.kt`, `ui/compass/*`, `ui/travel/*`, `ui/home/DiaryScreen.kt`, `ui/theme/*`, `widget/*` (Optik-Vorbild).
