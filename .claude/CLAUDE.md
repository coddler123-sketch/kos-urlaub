# kos-urlaub

## Release
Nach jedem Release zusätzlich die Debug-Version auf das angeschlossene Smartphone installieren (nur dort, nicht auf GitHub veröffentlichen):

1. `./gradlew.bat assembleDebug`
2. `adb install -r app/build/outputs/apk/debug/app-debug.apk` (adb: `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`)

Die Debug-App heißt „Kos DEBUG", hat die ID `...kosurlaub.debug` und die Version `x.y.z-debug`; sie läuft neben der Release-App. Ist kein Gerät verbunden (`adb devices` leer), das kurz melden statt abbrechen.
