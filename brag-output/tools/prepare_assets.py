"""Render real GoblinQuests game art into PNGs the brag composition can use.

- Renders the Tiled maps (town, cave) exactly from the project's .tmx + tilesets.
- Composites the layered character sprites (base + hair + tools) into strips.
All source files are read from the project's assets/ directory; nothing is invented.
"""
import os
import sys
import xml.etree.ElementTree as ET
from PIL import Image

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
ASSETS = os.path.join(ROOT, "assets")
TILED = os.path.join(ASSETS, "TiledProject")
OUT = os.path.join(ROOT, "brag-output", "composition", "assets", "game")
os.makedirs(OUT, exist_ok=True)

FLIP_H, FLIP_V, FLIP_D = 0x80000000, 0x40000000, 0x20000000
MASK = ~(FLIP_H | FLIP_V | FLIP_D) & 0xFFFFFFFF


class Tileset:
    def __init__(self, firstgid, tsx_path):
        self.firstgid = firstgid
        root = ET.parse(tsx_path).getroot()
        base = os.path.dirname(tsx_path)
        self.tw = int(root.get("tilewidth"))
        self.th = int(root.get("tileheight"))
        self.spacing = int(root.get("spacing", 0))
        self.margin = int(root.get("margin", 0))
        self.columns = int(root.get("columns", 0))
        self.tiles = {}  # collection tilesets: id -> (Image, type)
        self.sheet = None
        img = root.find("image")
        if img is not None:
            self.sheet = Image.open(os.path.join(base, img.get("source"))).convert("RGBA")
        else:
            for t in root.findall("tile"):
                i = t.find("image")
                self.tiles[int(t.get("id"))] = (
                    Image.open(os.path.join(base, i.get("source"))).convert("RGBA"),
                    t.get("type"),
                )

    def owns(self, gid):
        return gid >= self.firstgid

    def tile_image(self, gid):
        lid = gid - self.firstgid
        if self.sheet is not None:
            col, row = lid % self.columns, lid // self.columns
            x = self.margin + col * (self.tw + self.spacing)
            y = self.margin + row * (self.th + self.spacing)
            return self.sheet.crop((x, y, x + self.tw, y + self.th)), None
        return self.tiles.get(lid, (None, None))


def find_tileset(tilesets, gid):
    best = None
    for ts in tilesets:
        if ts.owns(gid) and (best is None or ts.firstgid > best.firstgid):
            best = ts
    return best


def orient(img, raw):
    if raw & FLIP_D:
        img = img.transpose(Image.TRANSPOSE)
    if raw & FLIP_H:
        img = img.transpose(Image.FLIP_LEFT_RIGHT)
    if raw & FLIP_V:
        img = img.transpose(Image.FLIP_TOP_BOTTOM)
    return img


def render_map(name, skip_object_types=("goblin", "bad_goblin", "skeleton"), draw_roofs=True):
    root = ET.parse(os.path.join(TILED, "maps", name + ".tmx")).getroot()
    W, H = int(root.get("width")), int(root.get("height"))
    TW, TH = int(root.get("tilewidth")), int(root.get("tileheight"))
    tilesets = [
        Tileset(int(t.get("firstgid")), os.path.normpath(os.path.join(TILED, "maps", t.get("source"))))
        for t in root.findall("tileset")
    ]
    canvas = Image.new("RGBA", (W * TW, H * TH), (0, 0, 0, 0))
    deferred_above = []

    def draw_layer(layer):
        data = layer.find("data").text.strip().replace("\n", "").split(",")
        for idx, v in enumerate(data):
            raw = int(v)
            gid = raw & MASK
            if gid == 0:
                continue
            ts = find_tileset(tilesets, gid)
            if ts is None:
                continue
            img, _ = ts.tile_image(gid)
            if img is None:
                continue
            img = orient(img, raw)
            x, y = (idx % W) * TW, (idx // W) * TH
            canvas.alpha_composite(img, (x, y + TH - img.height))

    def draw_objects(group):
        objs = []
        for o in group.findall("object"):
            if o.get("gid") is None:
                continue
            raw = int(o.get("gid"))
            gid = raw & MASK
            ts = find_tileset(tilesets, gid)
            if ts is None:
                continue
            img, ttype = ts.tile_image(gid)
            if img is None or ttype in skip_object_types:
                continue
            if "roof" in (group.get("name") or "").lower():
                continue
            w, h = round(float(o.get("width"))), round(float(o.get("height")))
            if (w, h) != img.size:
                img = img.resize((w, h), Image.NEAREST)
            img = orient(img, raw)
            x, y = float(o.get("x")), float(o.get("y"))
            objs.append((y, img, (round(x), round(y - h)), ttype, gid))
        objs.sort(key=lambda t: t[0])
        for y, img, pos, ttype, gid in objs:
            if not draw_roofs and ttype is None and img.size == (288, 192):
                continue
            canvas.alpha_composite(img, (max(pos[0], 0), max(pos[1], 0)))

    for child in root:
        if child.tag == "layer":
            if child.get("visible") == "0":
                continue
            if child.get("name") == "above":
                deferred_above.append(child)
            else:
                draw_layer(child)
        elif child.tag == "objectgroup" and child.get("visible") != "0":
            if child.get("name") in ("static_objects", "static_but_characteristic_objects", "objects"):
                draw_objects(child)
    for layer in deferred_above:
        draw_layer(layer)
    canvas.save(os.path.join(OUT, name + "_map.png"))
    print("map", name, canvas.size)


def sprite_strip(layers, state, frames, out_name, frame_w=96, frame_h=64):
    """Composite layered strips (base + hair + tools ...) into one strip."""
    strip = Image.new("RGBA", (frame_w * frames, frame_h), (0, 0, 0, 0))
    for layer in layers:
        path = os.path.join(ASSETS, "atlas", "raw", f"{layer}_{state}.png")
        if not os.path.exists(path):
            path = os.path.join(ASSETS, "atlas", "raw", "skeleton", f"{layer}_{state}.png")
        im = Image.open(path).convert("RGBA")
        strip.alpha_composite(im.crop((0, 0, frame_w * frames, frame_h)))
    strip.save(os.path.join(OUT, out_name))
    print("strip", out_name, strip.size)


if __name__ == "__main__":
    render_map("town")
    render_map("cave")
    player = ["base", "mophair", "tools"]
    sprite_strip(player, "idle", 9, "player_idle.png")
    sprite_strip(player, "run", 8, "player_run.png")
    sprite_strip(player, "death", 13, "player_death.png")
    sprite_strip(["badGoblin"], "idle", 8, "badgoblin_idle.png")
    sprite_strip(["badGoblin"], "run", 8, "badgoblin_run.png")
    sprite_strip(["badGoblin"], "attack", 9, "badgoblin_attack.png")
    sprite_strip(["skeleton"], "idle", 6, "skeleton_idle.png")
    sprite_strip(["skeleton"], "run", 8, "skeleton_run.png")
    sprite_strip(["boss"], "idle", 6, "boss_idle.png")
    sprite_strip(["boss"], "run", 8, "boss_run.png")
    # NPC goblins: 'spr' body + hair layer, as EntityFactory.createGoblin builds them (8 frames)
    sprite_strip(["spr", "curlyhair"], "idle", 8, "npc_quest_idle.png")
    sprite_strip(["spr", "longhair"], "idle", 8, "npc_smith_idle.png")
    sprite_strip(["spr", "bowlhair"], "idle", 8, "npc_def_idle.png")

    # Dialog panel: the 9x9 'dialog' nine-patch from the game's skin atlas (xy 1,461 size 9x9, split 2,2,2,2)
    skin = Image.open(os.path.join(ASSETS, "UI", "dialog", "skin", "skin.png")).convert("RGBA")
    skin.crop((1, 461, 10, 470)).save(os.path.join(OUT, "dialog9.png"))

    # Single-layer / simple pieces copied as-is
    for src, dst in [
        (os.path.join(ASSETS, "atlas", "raw", "gold.png"), "gold.png"),
        (os.path.join(TILED, "tilesets", "goblin_icon.png"), "goblin_icon.png"),
        (os.path.join(TILED, "tilesets", "duck_tired.png"), "duck_tired.png"),
        (os.path.join(TILED, "tilesets", "duck.png"), "duck.png"),
    ]:
        Image.open(src).convert("RGBA").save(os.path.join(OUT, dst))
