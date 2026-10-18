import base64, io
from PIL import Image

R = "../../app/src/main/res/drawable-nodpi/"


def img(n, w=640):
    im = Image.open(R + n + ".webp").convert("RGB")
    if im.width > w:
        im = im.resize((w, round(im.height * w / im.width)))
    b = io.BytesIO()
    im.save(b, "JPEG", quality=72)
    return "data:image/jpeg;base64," + base64.b64encode(b.getvalue()).decode()


names = ["kos_rot_01", "kos_rot_05", "excursion_marmari", "tip_zia_sunset", "kos_rot_11",
         "category_greek_food", "tip_antimachia_castle", "excursion_seven_springs", "kos_rot_08"]
P = {k: img(k) for k in names}

CSS = """
*{box-sizing:border-box;margin:0}
body{font-family:Inter,system-ui,sans-serif;background:#e9e6df;color:#1d2a2e;padding:24px 16px 60px}
@media(prefers-color-scheme:dark){body{background:#14181a;color:#e8e6e1}}
h1{font-size:26px;margin-bottom:6px}.lead{max-width:720px;opacity:.75;margin-bottom:28px;line-height:1.5}
h2{font-size:20px;margin:40px 0 4px}.sub{opacity:.7;margin-bottom:18px;max-width:720px;line-height:1.45}
.row{display:flex;gap:28px;overflow-x:auto;padding:6px 4px 16px}
.phone{flex:0 0 330px;height:680px;border-radius:38px;border:9px solid #111;background:#fff;overflow:hidden;position:relative;box-shadow:0 14px 40px #0004}
.cap{text-align:center;font-size:13px;opacity:.7;margin-top:8px;width:330px}
.fig{flex:0 0 auto}
.sb{height:26px;font-size:11px;display:flex;justify-content:space-between;padding:7px 18px 0;font-weight:600}
.A{background:#f6f0e4;color:#2b3a30}
.A h3,.A .serif{font-family:Fraunces,Georgia,serif}
.A .pol{background:#fff;padding:9px 9px 26px;box-shadow:0 6px 16px #0003;transform:rotate(-2.2deg);margin:14px 22px}
.A .pol img{display:block;width:100%;height:190px;object-fit:cover}
.A .pol p{font-family:Fraunces,serif;font-style:italic;font-size:13px;text-align:center;margin-top:8px}
.A .big{font-family:Fraunces,serif;font-size:76px;line-height:1;color:#1f5f7a;text-align:center;font-weight:700}
.A .chip{display:inline-block;background:#e6dcc4;border-radius:99px;padding:5px 11px;font-size:12px;margin:3px}
.A .card{background:#fffaf0;border:1px solid #e3d8bd;border-radius:14px;padding:12px;margin:8px 16px;font-size:13px;line-height:1.5}
.A .tl{display:flex;gap:6px;padding:8px 14px;overflow:hidden;position:absolute;bottom:0;left:0;right:0;background:#efe6d0;border-top:1px solid #d9cca8}
.A .tl span{flex:0 0 34px;height:44px;border-radius:8px;background:#fffaf0;font-size:10px;text-align:center;padding-top:6px;line-height:1.2;border:1px solid #dcd0b0}
.A .tl span.on{background:#1f5f7a;color:#fff}
.A .sos{position:absolute;right:12px;bottom:74px;background:#b8442f;color:#fff;border-radius:99px;padding:9px 14px;font-size:12px;font-weight:600;box-shadow:0 4px 10px #0004}
.B{background:#dfe9ec;color:#14303a}
.B .map{position:absolute;inset:0;width:100%;height:100%}
.B .pin{position:absolute;width:26px;height:26px;border-radius:50% 50% 50% 0;transform:rotate(-45deg);background:#c8553d;border:2px solid #fff;box-shadow:0 2px 6px #0004}
.B .pin.h{background:#14303a;width:32px;height:32px}
.B .top{position:absolute;top:32px;left:12px;right:12px;background:#fff;border-radius:14px;padding:10px 12px;font-size:13px;box-shadow:0 4px 12px #0003}
.B .chips{position:absolute;top:86px;left:12px;right:0;display:flex;gap:6px;overflow:hidden}
.B .chips span{background:#fff;border-radius:99px;padding:6px 12px;font-size:12px;white-space:nowrap;box-shadow:0 2px 6px #0003}
.B .chips span.on{background:#c8553d;color:#fff}
.B .sheet{position:absolute;left:0;right:0;bottom:0;background:#fff;border-radius:22px 22px 0 0;padding:10px 16px 18px;box-shadow:0 -6px 20px #0003}
.B .grab{width:40px;height:4px;background:#cbd5d8;border-radius:9px;margin:0 auto 10px}
.B .btn{display:inline-block;background:#c8553d;color:#fff;border-radius:10px;padding:9px 14px;font-size:13px;font-weight:600;margin-right:6px}
.B .btn.o{background:#fff;color:#14303a;border:1px solid #14303a}
.C{background:#fff;color:#0b0b0b;font-family:'Space Grotesk',Inter,sans-serif}
.C .hero{position:relative}.C .hero img{display:block;width:100%;height:300px;object-fit:cover}
.C .hero>div{position:absolute;left:14px;right:14px;bottom:14px;color:#fff;text-shadow:0 2px 12px #0009}
.C .k{font-size:11px;letter-spacing:.12em;text-transform:uppercase;color:#0a58b5;font-weight:700}
.C h3{font-size:25px;line-height:1.05;letter-spacing:-.02em}
.C .qb{display:flex;border-top:3px solid #000;border-bottom:1px solid #000;font-size:12px}
.C .qb div{flex:1;padding:8px 10px;border-right:1px solid #000}.C .qb div:last-child{border:0}
.C .qb b{display:block;font-size:17px}
.C .art{display:flex;gap:10px;padding:12px 14px;border-bottom:1px solid #ddd}
.C .art img{width:96px;height:96px;flex:0 0 96px;object-fit:cover}
.C .art p{font-family:Inter;font-size:12px;opacity:.65;margin-top:4px}
"""

ISLAND = '<path d="M250 150 C300 160 310 210 270 250 C240 290 190 330 150 380 C110 430 80 480 40 520 C20 540 10 520 30 490 C60 440 70 400 110 350 C150 300 170 250 200 200 C215 170 230 150 250 150Z" fill="#e8dfc2" stroke="#cdbf94" stroke-width="2"/>'
SB = '<div class="sb"><span>9:41</span><span>●●●</span></div>'


def tl(on):
    days = [("Mo", 13), ("Di", 14), ("Mi", 15), ("Do", 16), ("Fr", 17), ("Sa", 18), ("So", 19), ("Mo", 20)]
    return '<div class="tl">' + "".join(
        f'<span class="{"on" if d == on else ""}">{n}<br>{d}</span>' for n, d in days) + "</div>"


A1 = f"""<div class="fig"><div class="phone A">{SB}
<div style="text-align:center;margin-top:6px;font-size:12px;letter-spacing:.18em;opacity:.6">UNSER KOS · 2027</div>
<div class="pol"><img src="{P['kos_rot_01']}"><p>Kastri, irgendwann im September</p></div>
<div class="big">331</div><div style="text-align:center;font-family:Fraunces,serif;font-size:16px;margin-top:2px">Tage bis zum Abflug</div>
<div style="text-align:center;margin-top:8px"><span class="chip">13.09. · Hamburg 13:25</span><span class="chip">Condor DE1640</span></div>
<div class="card"><b class="serif">Eure Liste</b><br>☐ Online-Check-in<br>☐ Roaming prüfen<br><span style="opacity:.6">+ eigenen Punkt hinzufügen</span></div>
<div class="sos">SOS</div>{tl(0)}</div><div class="cap">Vorfreude-Modus: Tagebuchdeckel</div></div>"""

A2 = f"""<div class="fig"><div class="phone A">{SB}
<div style="padding:6px 16px"><div style="font-size:12px;opacity:.6">Tag 4 von 15</div>
<h3 style="font-size:28px;margin:2px 0 8px">Donnerstag, 16.09.</h3></div>
<div style="margin:0 16px;border-radius:14px;overflow:hidden"><img src="{P['excursion_marmari']}" style="display:block;height:150px;width:100%;object-fit:cover"></div>
<div class="card">☀ 28° · Wind 3 Bft · Meer 24° <span class="chip">Sonnenuntergang 19:42</span></div>
<div class="card"><b>Nächster Bus</b> ab Tigaki: 10:15 · 11:15<br><span style="opacity:.6">Referenz KTEL, Stand 28.09.2026</span></div>
<div class="card"><b>Euer Tag</b><br><span style="opacity:.6">Noch leer – ihr entscheidet.</span><br><span class="chip">+ Notiz</span><span class="chip">+ Foto</span><span class="chip">+ Ort von der Merkliste</span></div>
<div class="card" style="opacity:.75">▸ Ideen ansehen (optional, eingeklappt)</div>
<div class="sos">SOS</div>{tl(16)}</div><div class="cap">Vor-Ort-Modus: Tagesseite ohne Vorgaben</div></div>"""

A3 = f"""<div class="fig"><div class="phone A">{SB}
<div style="padding:6px 16px"><h3 style="font-size:26px">Unser Tagebuch</h3><div style="font-size:12px;opacity:.6">„Wollen wir noch“ · 4 von 14 erledigt</div></div>
<div class="pol"><img src="{P['tip_zia_sunset']}"><p>Zia, 16.09. · „Sonne weg um 19:42“</p></div>
<div class="card">✓ Asklepieion <span style="float:right;opacity:.5">14.09.</span></div>
<div class="card">✓ Taverne Tigaki <span style="float:right;opacity:.5">15.09.</span></div>
<div class="card">☐ Antimachia-Burg</div><div class="card">☐ Nisyros-Boot</div>
<div class="sos">SOS</div>{tl(16)}</div><div class="cap">Merkliste wird Tagebuch</div></div>"""

B1 = f"""<div class="fig"><div class="phone B"><div style="position:relative;z-index:2">{SB}</div>
<svg class="map" viewBox="0 0 330 680"><rect width="330" height="680" fill="#bcd9e3"/>{ISLAND}
<circle cx="215" cy="215" r="50" fill="none" stroke="#14303a66" stroke-dasharray="4 4"/><circle cx="215" cy="215" r="100" fill="none" stroke="#14303a44" stroke-dasharray="4 4"/>
<path d="M215 215 Q190 270 130 360 L95 440" stroke="#c8553d" stroke-width="3" fill="none" stroke-dasharray="7 4"/>
<text x="170" y="400" font-size="10" fill="#14303a99">schematisch · nicht maßstäblich</text></svg>
<div class="top">🔍 Insel durchsuchen</div>
<div class="chips"><span class="on">Alle</span><span>Heute offen</span><span>Regentag</span><span>Zu Fuß</span><span>Strand</span></div>
<div class="pin h" style="left:200px;top:215px"></div><div class="pin" style="left:240px;top:190px"></div><div class="pin" style="left:150px;top:310px"></div><div class="pin" style="left:128px;top:370px"></div><div class="pin" style="left:90px;top:430px"></div><div class="pin" style="left:255px;top:240px"></div>
<div style="position:absolute;left:100px;top:225px;font-size:11px;font-weight:600;background:#fff;border-radius:6px;padding:1px 5px">Hotel · Tigaki</div>
<div class="sheet"><div class="grab"></div><b>6 Tipps im Umkreis 30 Min.</b><div style="font-size:12px;opacity:.65;margin-top:3px">Ringe: 10 · 20 · 30 Min. ab Tigaki</div></div></div>
<div class="cap">Inselkarte mit Entfernungsringen</div></div>"""

B2 = f"""<div class="fig"><div class="phone B"><div style="position:relative;z-index:2">{SB}</div>
<svg class="map" viewBox="0 0 330 680"><rect width="330" height="680" fill="#bcd9e3"/>{ISLAND}</svg>
<div class="pin" style="left:215px;top:130px"></div>
<div class="sheet" style="height:470px"><div class="grab"></div>
<img src="{P['tip_zia_sunset']}" style="display:block;height:150px;width:100%;object-fit:cover;border-radius:12px">
<h3 style="font-size:20px;margin:10px 0 2px">Zia – Sonnenuntergang</h3>
<div style="font-size:12px;opacity:.7">Bergdorf · ca. 25 Min. ab Tigaki · beste Zeit 18:30–19:45</div>
<div style="margin:10px 0;font-size:13px;line-height:1.5">Aussicht über die Küste. Busverbindung nicht sicher bestätigt – vorab klären.</div>
<span class="btn">Route</span><span class="btn o">Merken</span></div></div>
<div class="cap">Tipp im Bottom-Sheet</div></div>"""

C1 = f"""<div class="fig"><div class="phone C">{SB}
<div class="hero"><img src="{P['kos_rot_11']}"><div><div class="k" style="color:#9fd0ff">Heute · Donnerstag 16.09.</div><h3>Unter Segeln nach Nisyros</h3></div></div>
<div class="qb"><div>Wetter<b>28°</b></div><div>Bus<b>10:15</b></div><div>Sonne<b>19:42</b></div><div>SOS<b>112</b></div></div>
<div class="art"><img src="{P['category_greek_food']}"><div><div class="k">Essen · 3 Min.</div><b>Fünf Klassiker zum Probieren</b><p>Saganaki, Pitaroudia, Melekouni …</p></div></div>
<div class="art"><img src="{P['tip_antimachia_castle']}"><div><div class="k">Ausflug · 4 Min.</div><b>Antimachia: die Burg im Nirgendwo</b><p>Ab Tigaki mit Bus oder Auto</p></div></div></div>
<div class="cap">Feed „Heute“</div></div>"""

C2 = f"""<div class="fig"><div class="phone C">{SB}
<div class="hero"><img src="{P['excursion_seven_springs']}" style="height:240px"><div><div class="k" style="color:#9fd0ff">Tigaki · 4 Min. Lesezeit</div><h3>Flamingos im Alikes-Salzsee</h3></div></div>
<div class="qb"><div>Dauer<b>1–2 h</b></div><div>Zeit<b>morgens</b></div><div>Ab Hotel<b>zu Fuß</b></div></div>
<div style="padding:14px;font-family:Inter;font-size:14px;line-height:1.6"><b>Ein ruhiger Morgen.</b> Der Salzsee liegt direkt bei Tigaki. Mit etwas Glück stehen Flamingos im flachen Wasser …
<div style="margin-top:12px"><span style="background:#000;color:#fff;padding:9px 14px;font-weight:700;font-size:13px">ROUTE</span> <span style="border:2px solid #000;padding:7px 12px;font-weight:700;font-size:13px">MERKEN</span></div></div></div>
<div class="cap">Tipp als Artikel</div></div>"""

C3 = f"""<div class="fig"><div class="phone C">{SB}
<div style="padding:10px 14px"><div class="k">Vorfreude · noch 331 Tage</div><h3 style="font-size:64px;line-height:.95;margin:4px 0">331<br>Tage.</h3><div style="font-family:Inter;font-size:13px;opacity:.7">Hamburg → Kos · Mo 13.09.2027 · 13:25</div></div>
<img src="{P['kos_rot_08']}" style="display:block;height:200px;width:100%;object-fit:cover">
<div class="art"><div><div class="k">Checkliste</div><b>Packen in 10 Punkten</b><p>23 kg aufgegeben · 8 kg Handgepäck</p></div></div>
<div class="art"><div><div class="k">Sprache</div><b>Kaliméra, Efcharistó – 12 Sätze</b><p>Mit Audio</p></div></div></div>
<div class="cap">Vorfreude-Modus</div></div>"""

html = f"""<!doctype html><html lang="de"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Kos Urlaub Mockups</title>
<link href="https://fonts.googleapis.com/css2?family=Fraunces:opsz,wght@9..144,500;9..144,700&family=Inter:wght@400;500;600&family=Space+Grotesk:wght@500;700&display=swap" rel="stylesheet">
<style>{CSS}</style></head><body>
<h1>Kos Urlaub – Mockups</h1>
<p class="lead">Drei Richtungen mit den echten Fotos der App. Texte und Zahlen sind Platzhalter, die Karte in B ist gezeichnet und nicht maßstäblich. Seitlich wischen für weitere Bildschirme.</p>
<h2>A – Reisetagebuch</h2><p class="sub">Hell, warm, persönlich. Navigation über die 15 Reisetage statt über Themen-Tabs.</p>
<div class="row">{A1}{A2}{A3}</div>
<h2>B – Karte zuerst</h2><p class="sub">Die Insel ist die Oberfläche. Tipps, Hotel und Buslinie liegen auf der Karte, Details im Bottom-Sheet.</p>
<div class="row">{B1}{B2}</div>
<h2>C – Tagesmagazin</h2><p class="sub">Redaktioneller Feed, große Fotos, harte Typografie. Tipps sind Artikel.</p>
<div class="row">{C1}{C2}{C3}</div>
</body></html>"""
open("kos-mockups.html", "w", encoding="utf-8").write(html)
print(len(html) // 1024, "KB")
