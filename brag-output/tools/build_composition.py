"""Generate the GoblinQuests brag composition (Hyperframes project).

Reads real game assets prepared by prepare_assets.py and writes:
  composition/index.html                 root: scene hosts + audio
  composition/compositions/<scene>.html  one sub-composition per scene

Everything on screen is a pure function of timeline time (no callbacks).
"""
import json
import math
import os
import random
import subprocess
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.abspath(os.path.join(HERE, "..", "composition"))
GAME = os.path.join(OUT, "assets", "game")
os.makedirs(os.path.join(OUT, "compositions"), exist_ok=True)

TOTAL = 20.0
MUSIC = "assets/music/happy-beats-business-moves-vol-1-by-ende-dot-app.mp3"

# ---------------------------------------------------------------- audio data
_audio_src = os.path.join(OUT, "assets", "data", "audio-data.json")
with open(_audio_src, "r", encoding="utf-8") as f:
    _ad = json.load(f)
_fps = _ad["fps"]
_frames = _ad["frames"][: int(TOTAL * _fps) + 2]
# keep only the first 20s in the project (the full file is 164s)
with open(_audio_src, "w", encoding="utf-8") as f:
    json.dump({"fps": _fps, "totalFrames": len(_frames), "frames": _frames}, f, separators=(",", ":"))


def _bass(i):
    b = _frames[i]["bands"]
    return max(b[0], b[1])


_MAXB = max(_bass(i) for i in range(len(_frames))) or 1.0


def levels(abs_start, dur, step=0.5):
    """Bass energy (0..1) sampled in `step` windows. Returns [[local_t, level], ...]."""
    out = []
    t = 0.0
    while t < dur - 1e-6:
        a = int((abs_start + t) * _fps)
        b = max(a + 1, int((abs_start + t + step) * _fps))
        vals = [_bass(i) for i in range(a, min(b, len(_frames)))]
        lv = (sum(vals) / len(vals)) / _MAXB if vals else 0.0
        out.append([round(t, 2), round(lv, 3)])
        t += step
    return out


# ---------------------------------------------------------------- audio events
AUDIO = []  # dicts: t, src, vol, dur
_rng = random.Random(7)
KEYS = [f"assets/sfx/keyboard/keypress-{n}.wav" for n in
        ["001", "002", "004", "005", "007", "008", "010", "011", "013", "016", "019", "022", "025", "028", "031"]]


_DUR_CACHE = {}


def file_len(rel):
    """Real length of an audio file (ffprobe), so data-duration matches the media."""
    if rel not in _DUR_CACHE:
        out = subprocess.run(
            ["ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "csv=p=0",
             os.path.join(OUT, rel)], capture_output=True, text=True)
        _DUR_CACHE[rel] = float(out.stdout.strip())
    return _DUR_CACHE[rel]


def sfx(t, path, vol, dur=None):
    src = "assets/sfx/" + path
    AUDIO.append({"t": round(t, 3), "src": src, "vol": vol, "dur": file_len(src) if dur is None else min(dur, file_len(src))})


def ticks(offset, text, t0, per, step, vol=0.26):
    for k in range(0, len(text), step):
        if text[k] == " ":
            continue
        src = _rng.choice(KEYS)
        AUDIO.append({"t": round(offset + t0 + k * per, 3), "src": src, "vol": vol, "dur": min(0.25, file_len(src))})


# ---------------------------------------------------------------- shared html
def esc(s):
    return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


def typed(prefix, text, cls="dlg-txt", extra_id=None):
    """Per-character spans (opacity driven by the timeline)."""
    k = 0
    out = []
    words = text.split(" ")
    for wi, w in enumerate(words):
        s = '<span class="w">'
        for ch in w:
            s += f'<span id="{prefix}-c{k}" class="c">{esc(ch)}</span>'
            k += 1
        s += "</span>"
        out.append(s)
        k += 1  # the space
    return f'<div id="{extra_id or prefix}" class="{cls}">' + " ".join(out) + "</div>"


def void_color(name):
    im = Image.open(os.path.join(GAME, name)).convert("RGB")
    return "#%02x%02x%02x" % im.getpixel((2, 2))


COMMON_CSS = """
@font-face{font-family:"Minecraft";src:url("assets/fonts/Minecraft.ttf") format("truetype");font-weight:400;font-style:normal}
#root{position:absolute;inset:0;overflow:hidden;background:#181425;font-family:"Minecraft",monospace;color:#ffffff}
#root *{box-sizing:border-box}
.cam,.zoom,.pan{position:absolute;left:0;top:0;width:0;height:0}
.cam{left:960px;top:410px}
.void{position:absolute;left:-4000px;top:-4000px;width:9600px;height:9600px}
.map{position:absolute;left:0;top:0;display:block;image-rendering:pixelated}
.spr{position:absolute;left:0;top:0;width:96px;height:64px;background-repeat:no-repeat;background-position:0px 0px;background-size:auto 64px;image-rendering:pixelated}
.actor{position:absolute;width:96px;height:64px}
.tint{position:absolute;inset:0;mix-blend-mode:multiply;z-index:5}
.glow{position:absolute;inset:0;z-index:6;opacity:0;background:radial-gradient(ellipse at 50% 42%,rgba(254,174,52,0) 40%,rgba(254,174,52,.55) 100%)}
.fade{position:absolute;inset:0;background:#000000;opacity:0;z-index:40}
.flash{position:absolute;inset:0;background:#ffffff;opacity:0;z-index:41}
.hud-gold{position:absolute;left:48px;top:36px;height:84px;padding:0 30px 0 20px;display:flex;align-items:center;gap:18px;background:#181425;border:4px solid #c0cbdc;font-size:56px;line-height:64px;color:#fee761;z-index:30}
.hud-gold img{width:64px;height:64px;image-rendering:pixelated;display:block}
.hud-gold .num{position:relative;width:104px;height:64px}
.hud-gold .num span{position:absolute;left:0;top:0}
.hud-clock{position:absolute;right:48px;top:36px;width:208px;height:100px;padding:6px 18px;background:#181425;border:4px solid #c0cbdc;font-size:38px;line-height:44px;text-align:right;color:#ffffff;z-index:30}
.hud-clock .day{display:block}
.hud-clock .tm{position:relative;display:block;height:44px}
.hud-clock .tm span{position:absolute;right:0;top:0}
.dlg{position:absolute;left:80px;right:80px;bottom:44px;height:210px;border:8px solid transparent;border-image:url("assets/game/dialog9.png") 2 fill / 8px / 0 stretch;background:#c0cbdc;background-clip:padding-box;image-rendering:pixelated;z-index:25}
.dlg.tall{height:304px}
.dlg-txt{position:absolute;left:36px;right:36px;top:28px;font-size:58px;line-height:74px;color:#524f5b}
.dlg-txt .w{display:inline-block;white-space:nowrap}
.dlg-txt .c{opacity:0}
.dlg-txt.on{opacity:1}
.row{position:relative;height:74px;white-space:nowrap}
.row .cur{display:inline-block;width:40px}
.banner{position:absolute;left:0;right:0;top:150px;text-align:center;z-index:22}
.plate{display:inline-block;padding:10px 32px 16px;background:#181425;border:6px solid #ffffff;font-size:76px;line-height:92px;color:#ffffff}
.plate .bw{display:inline-block}
"""

JS_HELPERS = """
const tl = gsap.timeline({ paused: true });
function typeLine(prefix, text, t0, per) {
  for (let k = 0; k < text.length; k++) {
    if (text[k] === " ") continue;
    tl.fromTo("#" + prefix + "-c" + k, { opacity: 0 }, { opacity: 1, duration: 0.001, ease: "none" }, t0 + k * per);
  }
}
// Elements that change state more than once take their initial state from CSS/inline style;
// every later tween uses immediateRender:false so it cannot overwrite that initial state.
function swap(ids, times) {
  for (let i = 1; i < ids.length; i++) {
    tl.fromTo(ids[i - 1], { opacity: 1 }, { opacity: 0, duration: 0.001, ease: "none", immediateRender: false }, times[i]);
    tl.fromTo(ids[i], { opacity: 0 }, { opacity: 1, duration: 0.001, ease: "none", immediateRender: false }, times[i]);
  }
}
function toggle(sel, t, on) {
  tl.fromTo(sel, { opacity: on ? 0 : 1 }, { opacity: on ? 1 : 0, duration: 0.001, ease: "none", immediateRender: false }, t);
}
function popIn(sel, t) {
  tl.fromTo(sel, { opacity: 0, y: 36 }, { opacity: 1, y: 0, duration: 0.22, ease: "power3.out" }, t);
}
function popOut(sel, t) {
  tl.fromTo(sel, { opacity: 1, y: 0 }, { opacity: 0, y: 18, duration: 0.15, ease: "power2.in", immediateRender: false }, t);
}
function anim(sel, frames, fd, t0, end) {
  const cyc = frames * fd;
  tl.fromTo(sel, { backgroundPosition: "0px 0px" },
    { backgroundPosition: (-96 * frames) + "px 0px", ease: "steps(" + frames + ")", duration: cyc,
      repeat: Math.max(0, Math.ceil((end - t0) / cyc) - 1) }, t0);
}
function glowBeats(sel, levels, base, amp) {
  // baseline opacity is 0 in CSS; each step eases to the next bass-driven level
  levels.forEach(function (p) {
    tl.to(sel, { opacity: base + amp * p[1], duration: 0.5, ease: "sine.inOut" }, p[0]);
  });
}
function camSet(zoom, pan, cx, cy, s) { gsap.set(zoom, { scale: s }); gsap.set(pan, { x: -cx, y: -cy }); }
function camMove(zoom, pan, from, to, t, dur, ease) {
  tl.fromTo(zoom, { scale: from[2] }, { scale: to[2], duration: dur, ease: ease, immediateRender: false }, t);
  tl.fromTo(pan, { x: -from[0], y: -from[1] }, { x: -to[0], y: -to[1], duration: dur, ease: ease, immediateRender: false }, t);
}
"""


def hud(sid, gold_states, clock_day, clock_times):
    gold = "".join(
        f'<span id="{sid}-g{i}" style="opacity:{1 if i == 0 else 0}">{v}</span>' for i, v in enumerate(gold_states))
    times = "".join(
        f'<span id="{sid}-t{i}" style="opacity:{1 if i == 0 else 0}">{v}</span>' for i, v in enumerate(clock_times))
    return (f'<div class="hud-gold"><img src="assets/game/gold.png" alt=""><div class="num">{gold}</div></div>'
            f'<div class="hud-clock"><span class="day">Day {clock_day}</span><span class="tm">{times}</span></div>')


def spr(cls_img, extra_style="", cid=""):
    return f'<div class="spr" id="{cid}" style="background-image:url(\'assets/game/{cls_img}\');{extra_style}"></div>'


def scene_file(sid, css, body, js):
    return f"""<template>
<style>
{COMMON_CSS}
{css}
</style>
<div id="root" data-composition-id="{sid}" data-width="1920" data-height="1080">
{body}
</div>
<script>
{JS_HELPERS}
{js}
window.__timelines["{sid}"] = tl;
</script>
</template>
"""


def write(name, content):
    with open(os.path.join(OUT, "compositions", name), "w", encoding="utf-8") as f:
        f.write(content)


# =============================================================== SCENE 1: QUEST
def build_quest():
    sid, off, dur = "quest", 0.0, 7.02
    L1, L2, THX = "Hi friend !", "I lost my golds ... some bad goblins stole my stuff !!", "thanks !"
    banner_words = ["Every", "RPG", "starts", "like", "this."]
    bw = " ".join(f'<span class="bw" id="quest-bw{i}">{w}</span>' for i, w in enumerate(banner_words))

    body = f"""
<div class="cam"><div class="zoom" id="quest-zoom"><div class="pan" id="quest-pan">
  <div class="void" style="background:#63c74d"></div>
  <img class="map" src="assets/game/town_map.png" alt="Town map rendered from town.tmx" width="1600" height="1120">
  <div class="actor" id="quest-gob" style="left:1162px;top:579px">{spr("npc_quest_idle.png", cid="quest-gob-s")}</div>
  <div class="actor" id="quest-pl" style="left:1102px;top:581px">
    {spr("player_idle.png", cid="quest-pl-idle")}
    {spr("player_run.png", "opacity:0;", cid="quest-pl-run")}
  </div>
</div></div></div>
<div class="tint" id="quest-tint" style="background:rgb(255,222,196)"></div>
<div class="glow" id="quest-glow"></div>
<div class="banner" id="quest-banner"><span class="plate">{bw}</span></div>
{hud(sid, [0], 1, ["08:00", "08:10", "08:20"])}
<div class="dlg" id="quest-dlg" style="opacity:0">
  {typed("quest-l1", L1)}
  {typed("quest-l2", L2)}
  <div class="dlg-txt" id="quest-ch" style="opacity:0">
    <div class="row"><span class="cur" id="quest-cur1">&gt;</span>Sure why not !</div>
    <div class="row"><span class="cur" id="quest-cur2" style="opacity:0">&gt;</span>Not my problem thanks for offer</div>
  </div>
  {typed("quest-thx", THX)}
</div>
<div class="fade" id="quest-fade" style="opacity:1"></div>
"""
    js = f"""
const L1 = {json.dumps(L1)}, L2 = {json.dumps(L2)}, THX = {json.dumps(THX)};
camSet("#quest-zoom", "#quest-pan", 1180, 592, 8);
gsap.set("#quest-pl", {{ transformOrigin: "48px 32px" }});

// world: open from black, push in and land (arrive slow, leave clean)
tl.fromTo("#quest-fade", {{ opacity: 1 }}, {{ opacity: 0, duration: 0.4, ease: "power2.out" }}, 0);
tl.fromTo("#quest-zoom", {{ scale: 6.8 }}, {{ scale: 8, duration: 0.9, ease: "expo.out" }}, 0);
anim("#quest-gob-s", 8, 0.14, 0, 7.02);
anim("#quest-pl-idle", 9, 0.14, 0, 6.3);

// hook banner: per-word blur-in, hold, blur-out
for (let i = 0; i < 5; i++) {{
  tl.fromTo("#quest-bw" + i, {{ opacity: 0, filter: "blur(8px)" }},
    {{ opacity: 1, filter: "blur(0px)", duration: 0.14, ease: "power2.out" }}, 0.35 + i * 0.09);
}}
tl.fromTo("#quest-banner", {{ opacity: 1, filter: "blur(0px)" }}, {{ opacity: 0, filter: "blur(8px)", duration: 0.2, ease: "power2.in" }}, 2.35);

// dialog panel + typed lines (0.03s per char, the game's own DialogBox speed)
popIn("#quest-dlg", 0.18);
typeLine("quest-l1", L1, 0.40, 0.03);
tl.fromTo("#quest-l1", {{ opacity: 1 }}, {{ opacity: 0, duration: 0.06 }}, 1.2);
typeLine("quest-l2", L2, 1.26, 0.03);
tl.fromTo("#quest-l2", {{ opacity: 1 }}, {{ opacity: 0, duration: 0.001 }}, 4.0);

// choice list, simulated on the beat grid (the cursor lingers on the refusal)
// beat-grid: choices 4.02s, cursor down 4.53s, cursor up 5.53s, confirm 6.03s
toggle("#quest-ch", 4.02, true);
toggle("#quest-cur1", 4.53, false); toggle("#quest-cur2", 4.53, true);
toggle("#quest-cur2", 5.53, false); toggle("#quest-cur1", 5.53, true);
toggle("#quest-ch", 6.04, false);
typeLine("quest-thx", THX, 6.08, 0.03);
tl.fromTo("#quest-thx", {{ opacity: 1 }}, {{ opacity: 0, duration: 0.001 }}, 6.86);
popOut("#quest-dlg", 6.78);

// the player takes the quest and runs off to the west
toggle("#quest-pl-idle", 6.3, false); toggle("#quest-pl-run", 6.3, true);
tl.fromTo("#quest-pl", {{ scaleX: 1 }}, {{ scaleX: -1, duration: 0.001 }}, 6.3);
anim("#quest-pl-run", 8, 0.1, 6.3, 7.02);
tl.fromTo("#quest-pl", {{ x: 0 }}, {{ x: -200, duration: 0.72, ease: "none" }}, 6.3);

// HUD clock: Day 1, morning
swap(["#quest-t0", "#quest-t1", "#quest-t2"], [0, 3.3, 6.0]);
tl.fromTo("#quest-tint", {{ backgroundColor: "rgb(255,222,196)" }}, {{ backgroundColor: "rgb(255,232,212)", duration: 7, ease: "none" }}, 0);

// the game's own map-change fade (black)
tl.fromTo("#quest-fade", {{ opacity: 0 }}, {{ opacity: 1, duration: 0.5, ease: "power2.in", immediateRender: false }}, 6.52);

glowBeats("#quest-glow", {json.dumps(levels(off, dur))}, 0.04, 0.2);
"""
    css = ""
    write("quest.html", scene_file(sid, css, body, js))

    # ---- audio (absolute)
    sfx(off + 0.14, "interface/drop_001.ogg", 0.5)
    ticks(off, L1, 0.40, 0.03, 3)
    ticks(off, L2, 1.26, 0.03, 3)
    sfx(off + 4.02, "interface/drop_002.ogg", 0.5)
    sfx(off + 4.53, "interface/click_003.ogg", 0.7, 0.3)
    sfx(off + 5.53, "interface/click_003.ogg", 0.7, 0.3)
    sfx(off + 6.03, "interface/select_008.ogg", 0.75, 0.5)
    ticks(off, THX, 6.08, 0.03, 3)
    for i, t in enumerate([6.42, 6.64, 6.86]):
        sfx(off + t, f"impact/footstep_wood_00{i % 2}.ogg", 0.3, 0.4)
    sfx(off + 6.9, "impact/impactSoft_medium_001.ogg", 0.4, 0.5)


# =============================================================== SCENE 2: CAVE
def build_cave():
    sid, off, dur = "cave", 7.02, 4.5
    goblins = json.load(open(os.path.join(HERE, "cave_goblins.json")))
    # centre of the opening shot; nearest goblins pop first
    cx0, cy0 = 330, 1440
    items = []
    for _, x, y, w, h in goblins:
        cxg, feet = x + w / 2, y  # object bottom == feet row
        items.append((((cxg - cx0) ** 2 + (feet - cy0) ** 2) ** 0.5, round(cxg - 48), round(y - 8 - 32)))
    items.sort()
    sizes = [4, 12, 24, 41]
    assert sum(sizes) == len(items) == 81
    waves, i = [], 0
    for n in sizes:
        waves.append(items[i:i + n])
        i += n
    html_g = []
    for wi, wave in enumerate(waves):
        # draw order: lower on screen = in front
        for k, (_, left, top) in enumerate(sorted(wave, key=lambda a: a[2])):
            html_g.append(
                f'<div class="spr cv-g cv-w{wi} cv-p{(k + wi) % 4}" style="left:{left}px;top:{top}px;'
                f'background-image:url(\'assets/game/badgoblin_idle.png\')"></div>')
    gobs = "\n  ".join(html_g)

    LINE = "I forgot the tell they are a lot of and has weapons sorry"
    # counter states per wave (rolls up over 0.3s after each wave pops)
    states, times = [0, 4, 16, 40, 81], [0.0, 1.0, 1.5, 2.0, 2.5]
    cnum = "".join(f'<span id="cave-n{i}" style="opacity:{1 if i == 0 else 0}">{v}</span>' for i, v in enumerate(states))

    body = f"""
<div class="cam" id="cave-cam"><div class="zoom" id="cave-zoom"><div class="pan" id="cave-pan">
  <div class="void" style="background:{void_color('cave_map.png')}"></div>
  <img class="map" src="assets/game/cave_map.png" alt="Cave map rendered from cave.tmx" width="1600" height="1600">
  {gobs}
  <div class="actor" id="cave-pl" style="left:31px;top:1431px">
    {spr("player_run.png", cid="cave-pl-run")}
  </div>
</div></div></div>
<div class="glow" id="cave-glow"></div>
<div class="counter" id="cave-counter"><span class="lbl">BAD GOBLINS</span><span class="big">{cnum}</span></div>
{hud(sid, [0], 1, ["09:40", "09:50"])}
<div class="dlg" id="cave-dlg" style="opacity:0">
  {typed("cave-l", LINE)}
</div>
<div class="fade" id="cave-fade" style="opacity:1"></div>
"""
    css = """
.counter{position:absolute;left:50%;top:36px;margin-left:-250px;width:500px;height:176px;background:#181425;border:6px solid #c0cbdc;text-align:center;z-index:20}
.counter .lbl{display:block;font-size:42px;line-height:48px;padding-top:12px;color:#ffffff}
.counter .big{position:relative;display:block;height:104px}
.counter .big span{position:absolute;left:0;right:0;top:0;font-size:104px;line-height:104px;color:#fee761}
"""
    js = f"""
const LINE = {json.dumps(LINE)};
camSet("#cave-zoom", "#cave-pan", 230, 1450, 5);
gsap.set("#cave-pl", {{ transformOrigin: "48px 32px" }});
gsap.set(".cv-g", {{ transformOrigin: "48px 39px" }});

tl.fromTo("#cave-fade", {{ opacity: 1 }}, {{ opacity: 0, duration: 0.4, ease: "power2.out" }}, 0);
anim("#cave-pl-run", 8, 0.1, 0, 4.5);
tl.fromTo("#cave-pl", {{ x: 0 }}, {{ x: 640, duration: 3.4, ease: "none" }}, 0.1);

// camera: pull back from the entrance to take in the whole cave
camMove("#cave-zoom", "#cave-pan", [230, 1450, 5], [790, 1000, 1.25], 0.3, 2.9, "power2.inOut");

// goblin idles, four phases so the swarm does not move in lockstep
for (let p = 0; p < 4; p++) anim(".cv-p" + p, 8, 0.14, 1.0 + p * 0.07, 4.5);

// beat-grid: wave 1 at 8.02s, wave 2 at 8.52s, wave 3 at 9.02s, wave 4 at 9.52s
for (let w = 0; w < 4; w++) {{
  tl.fromTo(".cv-w" + w, {{ scale: 0, opacity: 0 }},
    {{ scale: 1, opacity: 1, duration: 0.24, ease: "back.out(2)", stagger: 0.012 }}, 1.0 + w * 0.5);
}}
// counter rolls with each wave
swap({json.dumps([f"#cave-n{i}" for i in range(len(states))])}, {json.dumps(times)});

// the quest giver's fine print
popIn("#cave-dlg", 0.9);
typeLine("cave-l", LINE, 1.0, 0.022);

swap(["#cave-t0", "#cave-t1"], [0, 2.9]);

// seam out: horizontal smear (whip)
tl.fromTo("#cave-cam", {{ filter: "blur(0px)", x: 0 }}, {{ filter: "blur(14px)", x: -160, duration: 0.2, ease: "power3.in" }}, 4.3);

glowBeats("#cave-glow", {json.dumps(levels(off, dur))}, 0.04, 0.2);
"""
    write("cave.html", scene_file(sid, css, body, js))

    # ---- audio
    for wi, t in enumerate([1.0, 1.5, 2.0]):
        sfx(off + t, "interface/drop_003.ogg", 0.5 + 0.05 * wi, 0.5)
    sfx(off + 2.5, "impact/impactSoft_medium_004.ogg", 0.75, 0.5)
    sfx(off + 0.9, "interface/drop_001.ogg", 0.5)
    ticks(off, LINE, 1.0, 0.022, 4)
    sfx(off + 0.0, "impact/impactSoft_medium_001.ogg", 0.35, 0.5)


# =============================================================== SCENE 3: SHOP + HIDEOUT
def build_shop():
    sid, off, dur = "shop", 11.52, 4.5
    BOSS = "But its pricey like 50 golds"
    body = f"""
<div class="cam" id="shop-cam"><div class="zoom" id="shop-zoom"><div class="pan" id="shop-pan">
  <div class="void" style="background:#63c74d"></div>
  <img class="map" src="assets/game/town_map.png" alt="Town map rendered from town.tmx" width="1600" height="1120">
  <div class="actor" style="left:440px;top:66px">{spr("npc_smith_idle.png", cid="shop-smith")}</div>
  <div class="actor" style="left:414px;top:95px">{spr("player_idle.png", cid="shop-pl")}</div>
  <div class="actor" style="left:1370px;top:282px">{spr("boss_idle.png", cid="shop-boss")}</div>
  <div class="actor" style="left:1442px;top:227px">{spr("skeleton_idle.png", cid="shop-sk1")}</div>
  <div class="actor" style="left:1255px;top:227px">{spr("skeleton_idle.png", cid="shop-sk2")}</div>
  <div class="actor" id="shop-sk3w" style="left:1476px;top:168px">{spr("skeleton_idle.png", cid="shop-sk3")}</div>
</div></div></div>
<div class="tint" id="shop-tint" style="background:rgb(255,187,132)"></div>
<div class="glow" id="shop-glow"></div>
{hud(sid, [50, 0], 1, ["18:20", "18:30"])}
<div class="dlg tall" id="shop-dlg" style="opacity:0">
  <div class="dlg-txt" id="shop-list">
    <div class="row" id="shop-h0">Gold: 50</div>
    <div class="row" id="shop-h1" style="opacity:0;position:absolute;left:0;top:0">Gold: 0 - Thanks!</div>
    <div class="row"><span class="cur">&gt;</span>Skeleton Chip - 50g</div>
    <div class="row"><span class="cur">&nbsp;</span>Leave</div>
  </div>
</div>
<div class="dlg" id="boss-dlg" style="opacity:0">
  {typed("boss-l", BOSS)}
</div>
<div class="flash" id="shop-flash"></div>
"""
    css = ""
    js = f"""
const BOSS = {json.dumps(BOSS)};
camSet("#shop-zoom", "#shop-pan", 488, 100, 8);
gsap.set("#shop-sk3w", {{ transformOrigin: "48px 32px", scaleX: -1 }});

// arrive from the whip, slightly large, then settle
tl.fromTo("#shop-cam", {{ filter: "blur(14px)", x: 140 }}, {{ filter: "blur(0px)", x: 0, duration: 0.3, ease: "power3.out" }}, 0);
tl.fromTo("#shop-zoom", {{ scale: 8.8 }}, {{ scale: 8, duration: 0.5, ease: "expo.out" }}, 0);
anim("#shop-smith", 8, 0.14, 0, 4.5);
anim("#shop-pl", 9, 0.14, 0, 4.5);
anim("#shop-boss", 6, 0.16, 0, 4.5);
anim("#shop-sk1", 6, 0.16, 0.05, 4.5);
anim("#shop-sk2", 6, 0.16, 0.1, 4.5);
anim("#shop-sk3", 6, 0.16, 0.15, 4.5);

// the shop panel (real text from DialogInteractSystem.enterShop)
popIn("#shop-dlg", 0.04);
// beat-locked: purchase confirm 12.52s (local 1.00)
toggle("#shop-h0", 1.0, false); toggle("#shop-h1", 1.0, true);
swap(["#shop-g0", "#shop-g1"], [0, 1.0]);
popOut("#shop-dlg", 2.25);

// beat-locked: whip pan to the skeleton hideout at 13.52s (local 2.00)
camMove("#shop-zoom", "#shop-pan", [488, 100, 8], [1405, 262, 5.5], 2.0, 0.5, "power3.inOut");
tl.fromTo("#shop-cam", {{ filter: "blur(0px)" }}, {{ filter: "blur(16px)", duration: 0.22, ease: "power2.in", immediateRender: false }}, 2.0);
tl.fromTo("#shop-cam", {{ filter: "blur(16px)" }}, {{ filter: "blur(0px)", duration: 0.25, ease: "power2.out", immediateRender: false }}, 2.22);

// the boss's price line, HUD still says 0
popIn("#boss-dlg", 2.4);
typeLine("boss-l", BOSS, 2.5, 0.022);

swap(["#shop-t0", "#shop-t1"], [0, 2.6]);

// seam out: flash on the 16.02s strong cue
glowBeats("#shop-glow", {json.dumps(levels(off, dur))}, 0.04, 0.2);
"""
    write("shop.html", scene_file(sid, css, body, js))

    # ---- audio
    sfx(off + 0.04, "interface/drop_001.ogg", 0.45)
    sfx(off + 1.0, "interface/select_008.ogg", 0.65, 0.5)
    sfx(off + 1.02, "casino/chips-collide-1.ogg", 0.7, 0.8)
    sfx(off + 1.2, "casino/chip-lay-1.ogg", 0.45, 0.5)
    sfx(off + 1.96, "casino/card-slide-1.ogg", 0.5, 0.6)
    sfx(off + 2.4, "interface/drop_001.ogg", 0.45)
    ticks(off, BOSS, 2.5, 0.022, 3)


# =============================================================== SCENE 4: OUTRO
def build_outro():
    sid, off, dur = "outro", 16.02, 3.98
    DEV = "Developer forgot the set my identity :)"
    body = f"""
<div class="cam"><div class="zoom" id="outro-zoom"><div class="pan" id="outro-pan">
  <div class="void" style="background:#63c74d"></div>
  <img class="map" src="assets/game/town_map.png" alt="Town map rendered from town.tmx" width="1600" height="1120">
  <div class="actor" style="left:857px;top:486px">{spr("player_idle.png", cid="outro-pl")}</div>
  <div class="actor" style="left:932px;top:486px">{spr("npc_def_idle.png", cid="outro-gob")}</div>
</div></div></div>
<div class="tint" id="outro-tint" style="background:rgb(255,187,132)"></div>
<div class="glow" id="outro-glow"></div>
<div class="titlewrap">
  <div class="halo" id="outro-halo"></div>
  <div class="plaque" id="outro-plaque">GoblinQuests</div>
  <div class="sub" id="outro-sub"><span>made with libGDX</span></div>
</div>
{hud(sid, [0], 1, ["20:30"])}
<div class="dlg" id="outro-dlg" style="opacity:0">
  {typed("outro-l", DEV)}
</div>
<div class="fade" id="outro-fade"></div>
<div class="flash" id="outro-flash" style="opacity:1"></div>
"""
    css = """
.titlewrap{position:absolute;left:0;right:0;top:150px;height:330px;z-index:22;text-align:center}
.halo{position:absolute;left:50%;top:-40px;width:1500px;height:380px;margin-left:-750px;opacity:0;background:radial-gradient(ellipse at 50% 50%,rgba(254,174,52,.7) 0%,rgba(254,174,52,0) 68%)}
.plaque{position:relative;display:inline-block;padding:14px 56px 28px;background:#181425;border:8px solid #feae34;font-size:176px;line-height:190px;color:#feae34}
.sub{position:relative;margin-top:26px}
.sub span{display:inline-block;padding:8px 28px 12px;background:#181425;border:4px solid #c0cbdc;font-size:46px;line-height:56px;color:#c0cbdc}
"""
    js = f"""
const DEV = {json.dumps(DEV)};
camSet("#outro-zoom", "#outro-pan", 905, 500, 6);
anim("#outro-pl", 9, 0.14, 0, 3.98);
anim("#outro-gob", 8, 0.14, 0, 3.98);

// beat-locked: title slam on the 16.02s strong cue; colour flood to dusk
tl.fromTo("#outro-flash", {{ opacity: 0.9 }}, {{ opacity: 0, duration: 0.24, ease: "power2.out" }}, 0);
tl.fromTo("#outro-plaque", {{ opacity: 0, scale: 1.22 }}, {{ opacity: 1, scale: 1, duration: 0.2, ease: "expo.out" }}, 0);
tl.fromTo("#outro-tint", {{ backgroundColor: "rgb(255,187,132)" }}, {{ backgroundColor: "rgb(140,115,166)", duration: 0.5, ease: "power2.inOut" }}, 0);
// the world moves, the title does not
tl.fromTo("#outro-pan", {{ x: -905, y: -500 }}, {{ x: -925, y: -500, duration: 3.98, ease: "none" }}, 0);

tl.fromTo("#outro-sub", {{ opacity: 0, filter: "blur(8px)" }}, {{ opacity: 1, filter: "blur(0px)", duration: 0.2, ease: "power2.out" }}, 0.7);

// beat-locked: dialog panel on the 17.02s strong cue
popIn("#outro-dlg", 1.0);
typeLine("outro-l", DEV, 1.15, 0.03);

// audio-reactive halo behind the title
glowBeats("#outro-halo", {json.dumps(levels(off, dur))}, 0.25, 0.55);
glowBeats("#outro-glow", {json.dumps(levels(off, dur))}, 0.03, 0.14);

// clean ending
tl.fromTo("#outro-fade", {{ opacity: 0 }}, {{ opacity: 1, duration: 0.33, ease: "power2.in" }}, 3.62);
"""
    write("outro.html", scene_file(sid, css, body, js))

    # ---- audio
    sfx(off - 0.02, "impact/impactSoft_medium_001.ogg", 0.7, 0.5)
    sfx(off + 0.0, "impact/impactBell_heavy_000.ogg", 0.75, 2.5)
    sfx(off + 1.0, "interface/drop_001.ogg", 0.5)
    ticks(off, DEV, 1.15, 0.03, 3)


# =============================================================== ROOT
SCENES = [
    ("quest", 0.0, 7.02, "compositions/quest.html"),
    ("cave", 7.02, 4.5, "compositions/cave.html"),
    ("shop", 11.52, 4.5, "compositions/shop.html"),
    ("outro", 16.02, 3.98, "compositions/outro.html"),
]


def build_root():
    hosts = "\n".join(
        f'    <div id="{sid}" class="clip" data-composition-id="{sid}" data-composition-src="{src}" '
        f'data-start="{st}" data-duration="{du}" data-track-index="{i + 1}" data-track-kind="graphics" '
        f'data-width="1920" data-height="1080"></div>'
        for i, (sid, st, du, src) in enumerate(SCENES))

    auds = ['    <audio id="bgm" class="clip" data-start="0" data-duration="20" data-track-index="10" '
            f'data-volume="0.34" data-fade-in="0.5" data-fade-out="1.6" src="{MUSIC}"></audio>']
    lane_end = []  # interval partitioning: no two overlapping clips share a track
    for i, a in enumerate(sorted(AUDIO, key=lambda a: a["t"])):
        t = max(0.0, a["t"])
        d = math.floor(min(a["dur"], TOTAL - t) * 1000) / 1000
        lane = next((n for n, e in enumerate(lane_end) if e <= t + 1e-6), None)
        if lane is None:
            lane_end.append(0.0)
            lane = len(lane_end) - 1
        lane_end[lane] = t + d
        auds.append(
            f'    <audio id="sfx{i:03d}" class="clip" data-start="{t:.3f}" data-duration="{d:.3f}" '
            f'data-track-index="{11 + lane}" data-volume="{a["vol"]}" src="{a["src"]}"></audio>')

    variables = json.dumps([
        {"id": "color-ground", "type": "color", "label": "Background", "default": "#181425"},
        {"id": "color-ink", "type": "color", "label": "Dialog text", "default": "#524f5b"},
        {"id": "color-accent", "type": "color", "label": "Accent (gold)", "default": "#feae34"},
        {"id": "font-display", "type": "string", "label": "Display font", "default": "Minecraft"},
    ])
    html = f"""<!doctype html>
<html lang="en" data-composition-variables='{variables}'>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=1920, height=1080" />
    <title>GoblinQuests - brag</title>
    <script src="assets/vendor/gsap.min.js"></script>
    <style>
      :root {{ --color-ground: #181425; --color-ink: #524f5b; --color-accent: #feae34; --font-display: "Minecraft"; }}
      * {{ margin: 0; padding: 0; box-sizing: border-box; }}
      html, body {{ margin: 0; width: 1920px; height: 1080px; overflow: hidden; background: var(--color-ground); }}
    </style>
  </head>
  <body>
    <div id="root" data-composition-id="main" data-start="0" data-duration="{TOTAL}" data-width="1920" data-height="1080">
{hosts}
{chr(10).join(auds)}
    </div>
    <script>
      window.__timelines = window.__timelines || {{}};
      const tl = gsap.timeline({{ paused: true }});
      window.__timelines["main"] = tl;
    </script>
  </body>
</html>
"""
    with open(os.path.join(OUT, "index.html"), "w", encoding="utf-8") as f:
        f.write(html)


if __name__ == "__main__":
    build_quest()
    build_cave()
    build_shop()
    build_outro()
    build_root()
    print("scenes written; audio events:", len(AUDIO))
