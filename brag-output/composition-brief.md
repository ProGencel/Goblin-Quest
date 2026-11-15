# Hyperframes Composition Brief: GoblinQuests

## Objective
Create a short launch-style brag video for GoblinQuests.

## Output
- Composition directory: `brag-output/composition/`
- Rendered video: `brag-output/brag.mp4`
- Format: landscape — 1920x1080
- Duration: 20.0 seconds

## Source Material
- Project root: `C:\Desktop\libGDX\GoblinQuests`
- Primary files read: `README.md`, `assets/data/dialogues.json`, `assets/data/shops.json`, `assets/TiledProject/maps/{town,cave}.tmx` (+ tilesets), `assets/atlas/raw/*`, `assets/UI/dialog/skin/{skin.json,skin.png,skin.atlas}`, `core/.../screens/GameScreen.java`, `ui/InGameUi.java`, `hud/{Hud,ShopManager}.java`, `hud/customActors/{DialogBox,ClockLabel}.java`, `systems/{DayNightSystem,RenderSystem,DialogInteractSystem,AnimationSystem}.java`, `factories/EntityFactory.java`
- Product name: GoblinQuests
- Tagline / strongest claim: none in the project; the strongest copy is the NPC dialogue ("I forgot the tell they are a lot of and has weapons sorry")
- Key UI or visual moment to recreate: the nine-patch dialog panel with typed text and the choice list; the shop list; the gold + clock HUD; the town and cave maps
- Copy that must appear verbatim (typos are the point):
  - "Hi friend !"
  - "I lost my golds ... some bad goblins stole my stuff !!" (the real line, trimmed)
  - "Sure why not !" / "Not my problem thanks for offer"
  - "thanks !"
  - "I forgot the tell they are a lot of and has weapons sorry"
  - "Gold: 50", "Skeleton Chip - 50g", "Leave", "Gold: 0 - Thanks!"
  - "But its pricey like 50 golds"
  - "Developer forgot the set my identity :)"
  - Count: 81 bad goblins (number of `bad_goblin` objects in `cave.tmx`)

## Creative Direction
- Tone preset: default
- Creative direction: honest RPG trailer where the game's own dialogue does all the talking
- Interpretation: warm, dry, postable; comfortable holds on typed text, fast whip/fade seams, one slam at the end
- Angle: "Every RPG starts like this." Then a recreated playthrough made entirely from the real game assets: the quest giver forgets the cave is full of armed goblins, the fix costs 50g, and the last NPC is one the developer never configured.
- Hook: banner "Every RPG starts like this." + typed "Hi friend !" on the quest goblin
- Outro / punchline: GoblinQuests title slam, then "Developer forgot the set my identity :)"
- Avoid:
  - Generic SaaS language
  - Abstract filler visuals
  - Unrelated visual redesign (everything on screen is rendered from the project's own maps, sprites, panel and font)

## Visual Identity
- Background: `#181425`
- Text: `#524f5b` on `#c0cbdc` (dialog panel); white with `#181425` pixel shadow for HUD / banner
- Accent: `#fee761` (coin gold), `#feae34` (title)
- Display font: Minecraft (`assets/fonts/Minecraft.ttf`, copied from the game)
- Body font: Minecraft
- Visual references from the project: town map (grass/dirt/river), cave map (rust floor, dark void), 96x64 sprite strips, 9x9 `dialog` nine-patch (slice 2), gold coin HUD, `Day N / HH:MM` clock, day-night multiply tint

## Storyboard
Use the storyboard in `brag-output/brag-plan.md` as the creative contract.

Scene summary:
1. Quest — 7.02s — town close-up, typed hook, choice menu simulated on consecutive beats
2. Cave — 4.5s — pull-back from the entrance, 81 bad goblins in four beat-locked waves, counter, goblin's fine print
3. The business — 4.5s — shop panel, purchase (50 → 0), whip-pan to the skeleton hideout, boss's price line
4. Outro — 3.98s — title slam on 16.02s, dusk, final dialogue line, fade

## Audio
- Audio role: warm bed with motion-matched accents
- Audio arc: quiet intro under typed text → beat kicks in at 3.02s → clicks/drops on the grid → bell + thud on the 16.02s slam → fade out
- Music: `assets/music/happy-beats-business-moves-vol-1-by-ende-dot-app.mp3` (Sascha Ende, ende.app, CC BY 4.0)
- Music treatment: volume 0.34, 0.5s fade-in, 1.6s fade-out
- Music cue guidance: bundled preset `skills/brag/assets/music/cues/happy-beats-business-moves-vol-1-by-ende-dot-app.music-cues.json` (120.19 BPM; strong cues 16.02, 17.02, 18.02, 20.02s). Locks: title slam 16.02s, outro panel 17.02s, shop confirm 12.52s. Beat-grid: choice cursor 4.53/5.03/5.53s; goblin waves 7.52/8.02/8.52/9.02s.
- Audio-reactive treatment: subtle; bass energy modulates a faint warm vignette glow and the golden halo behind the title. No waveform or equalizer visuals. Data: `assets/data/audio-data.json` (extracted with `media-use/audio/scripts/extract-audio-data.py`), baked into the scenes at build time.
- Audio-coupled moments:
  - typed dialogue — quiet keyboard ticks every 3rd-4th character
  - choice cursor — clicks on three consecutive beats
  - goblin waves — drops on four consecutive beats, soft thud on the biggest wave
  - purchase — coin chips as the gold HUD counts down
  - title slam — bell + soft thud on 16.02s
- SFX selection guidance: low/medium HF-risk picks only (`interface/drop_*`, `interface/click_003`, `interface/select_008`, `impact/impactSoft_medium_*`, `impact/impactBell_heavy_000`, `casino/chips-collide-*`, `keyboard/keypress-*`), volumes 0.28-0.8
- SFX analysis guidance: `skills/brag/assets/sfx/sfx-analysis.md`
- Exact SFX choice: filenames, timestamps and volumes are in `composition/index.html`
- Audio files: copied into `composition/assets/{music,sfx}/`

## Hyperframes Instructions
Built with the current Hyperframes contract (`hyperframes` format/motion/look references, `hyperframes-cli` check, `media-use` audio data). /brag is its own workflow: no storyline step, no SPEC.md, no usage/history/feedback/REVIEW steps; `check` is the gate.

Requirements:
- Show real UI/art from the project (done: maps, sprites, dialog nine-patch, font, HUD).
- All text readable; dialogue holds ≥ 2s after it finishes typing where it carries the joke.
- 15–25s total (20.0s).
- Music + SFX included; audio-reactive glow present.
- Everything is a pure function of timeline time: no callbacks, no `repeat: -1`, no random at render time.
- Keep creation and rendering local.

## Build notes
- The composition is generated by `brag-output/tools/build_composition.py` from real game assets prepared by `brag-output/tools/prepare_assets.py` (town/cave maps rendered from the Tiled files with their own tilesets; sprite strips composited from `assets/atlas/raw`).
