# Brag Plan: GoblinQuests

## What is this app?
A top-down pixel-art RPG built with libGDX (Ashley ECS, Box2D, Tiled maps) where every NPC in town is a little bit unreliable: the quest giver forgets to mention the cave holds a lot of armed goblins, so you end up hiring skeletons.

## The angle
"Every RPG starts like this", except this one is told entirely in the game's own dialogue boxes. The video is a recreated playthrough built from the real Tiled maps, real sprites, the real nine-patch dialog panel, the real Minecraft bitmap font, the real HUD (gold counter and `Day 1 / 08:20` clock) and the real day/night tint curve. The jokes are the game's own copy, in its own broken English. Nothing is invented about the product; the framing line is the only made-up copy.

The quest giver promises an easy job ("some bad goblins stole my stuff !!"), and then forgets the important part ("they are a lot of and has weapons sorry"). The weaponsmith's solution is a **Skeleton Chip** for 50g, and the skeleton boss immediately charges "50 golds" to a player whose HUD says `0`. It ends on the game's fallback line for an NPC the developer never configured.

## Hook (first 2-3 seconds)
Black fades up on the town. A banner reads **"Every RPG starts like this."** while a goblin NPC's dialog panel pops and types the game's real first words: **"Hi friend !"** Dead-simple, instantly recognisable, and the typed Minecraft-font text gives the first seconds motion and sound (key ticks).

## Key moments (the middle)
- **Quest line, typed live:** "I lost my golds ... some bad goblins stole my stuff !!" (trimmed from the real intro line in `assets/data/dialogues.json`).
- **The choice menu, simulated:** the real `> Sure why not !` / `Not my problem thanks for offer` list. The cursor drifts down to the refusal, comes back, and confirms on consecutive beats.
- **The cave reveal:** the camera pulls back from the player at the cave entrance while **81 bad goblins** (the exact count of bad_goblin objects in `cave.tmx`) pop in wave by wave on the beat, with a counter rolling up. The goblin NPC's line lands: "I forgot the tell they are a lot of and has weapons sorry".
- **The business:** weaponsmith shop panel (`Gold: 50` / `> Skeleton Chip - 50g` / `Leave`), buy, gold HUD ticks 50 → 0 with "Thanks!". Whip-pan across the map to the skeleton hideout: the boss says "But its pricey like 50 golds" with `0` still on the HUD.

## Outro / punchline
**GoblinQuests** slams in on the strongest music cue (16.02s), the town flips to dusk, and the last dialog box delivers the game's own fallback for an NPC with no identity: **"Developer forgot the set my identity :)"** Small line: "made with libGDX". Hold, fade.

## User flow worth showing
1. **Entry:** walk up to the quest goblin, talk (typed dialogue, nine-patch panel).
2. **Key action:** pick "Sure why not !" from the choice list → enter the cave full of bad goblins → buy the Skeleton Chip from the weaponsmith (gold 50 → 0) → talk to the skeleton boss.
3. **Result:** the quest giver's fine print, the price tag, and an NPC the developer forgot to configure.

## Tone
- Preset: `default`
- Creative direction: honest RPG trailer where the game's own dialogue does all the talking
- Interpretation: warm, dry and postable. Comfortable holds on typed text (the game's own pacing), fast whip/fade transitions, one big slam at the end. No sarcasm beyond what the NPCs already say.

## Format: landscape — 1920x1080
(The game viewport is 480x270, so a 4x pixel scale fits the frame exactly. Close-ups use 5-6x.)
## Duration: 20.0 seconds

## Visual identity (from the project)
- Background / outline ink: `#181425` (the dialog panel border; also the dark cave void)
- Panel fill: `#c0cbdc`, panel inner border `#ffffff`
- Dialog text: `#524f5b` (skin.json `FontColor`)
- Accent: gold `#fee761` / `#feae34` (from `gold.png`, the HUD coin)
- World colours: grass `#63c74d`, path `#c2856a` (real town render), cave rock `#9b5f50`
- Display font: **Minecraft** (`assets/UI/dialog/font/Minecraft.ttf`, the in-game font)
- Body font: Minecraft (same; the game uses one font everywhere)
- Strongest visual element: the real town and cave maps rendered from `town.tmx` / `cave.tmx` with the project's own sprites, the nine-patch dialog box, and the gold/clock HUD.

## Share copy (draft)
Made GoblinQuests, a tiny libGDX RPG where the quest giver forgot to mention the 81 armed goblins. "Developer forgot the set my identity :)"

## Audio direction
- Role: warm bed plus motion-matched accents. The game's typed dialogue is the sonic backbone.
- Music: `happy-beats-business-moves-vol-1-by-ende-dot-app.mp3` (120 BPM, upbeat, clean; fits `default`).
- Music treatment: starts at 0:00 with a 0.5s fade-in at about 0.34 volume. The track's own 3s soft intro sits under the hook, and the beat grid starts at 3.02s. A 1.6s fade-out to the end. A slightly bigger swell lands with the strong cue at 16.02s.
- Music cue guidance: preset read from `skills/brag/assets/music/cues/happy-beats-business-moves-vol-1-by-ende-dot-app.music-cues.md` (120.19 BPM; beat grid from 3.02s every ~0.5s; strong cues at 16.02, 17.02, 18.02 and 20.02s). Beat-lock the title slam to **16.02s**, and the outro dialog pop to **17.02s**. Sequential events use consecutive beats: choice cursor at 4.53 / 5.03 / 5.53s; goblin waves at 7.52 / 8.02 / 8.52 / 9.02s; shop purchase at 12.52s; whip pan at 13.52s.
- Audio-reactive treatment: subtle. Bass energy from the music drives a faint warm vignette glow over the world in every scene and the golden halo behind the title. No waveform or equalizer visuals.
- SFX posture: moderate, motion-matched, low-HF-risk picks. Keyboard ticks on typed dialogue (every 3rd-4th character, quiet), soft drops for panels and goblin waves, clicks for the menu, coins for the purchase, a soft thud and a bell for the title.
- Audio-coupled moments: typed dialogue (ticks), choice cursor (clicks on beats), goblin waves (drops on beats, counter rolls), purchase (coin chips), title slam (bell + thud on the 16.02s cue).
- Restraint rule: ticks stay quiet under the music and never overlap an impact; no SFX on the whip-pan seam except one soft swish.

## Storyboard

### Scene 1 — Quest (hook + choice) — 7.02s (0.00-7.02)
Town, close-up (6x) on the quest goblin and the player standing in the dirt clearing. Black fades up (0-0.4s). Banner top: **"Every RPG starts like this."** (0.3-2.3s). Dialog panel pops at 0.2s: "Hi friend !" types (0.4s, hold to 1.35s), then "I lost my golds ... some bad goblins stole my stuff !!" types from 1.4s (hold fully typed 3.0-4.0s). At 4.02s the panel switches to the real choice list: `> Sure why not !` / `  Not my problem thanks for offer`. Cursor moves to the refusal at 4.53s, back at 5.03s, confirms at 5.53s; the panel says "thanks !" and closes. Player runs off left, screen fades to black (6.45-7.02s). HUD: gold `0`, clock `Day 1 08:00 → 08:20`, morning tint.
Sequential/interaction: yes — typed dialogue, choice cursor on consecutive beats (4.53 / 5.03 / 5.53).
Audio intent: warm, curious. Ticks under the typing, tiny clicks on the cursor.
Audio-coupled idea: typed text with key ticks; cursor clicks on beats.
Music: soft intro, beat arrives at 3.02s.
Transition mood: soft fade through black (the game's own map-change fade) → Scene 2

### Scene 2 — Cave (the fine print) — 4.5s (7.02-11.52)
Fade up on the cave entrance. Player runs along the corridor (5x). Bad goblins pop in four waves on the beat (7.52 / 8.02 / 8.52 / 9.02s), nearest first, while the camera pulls back from 5x to 1x. A counter rolls **BAD GOBLINS 4 → 16 → 40 → 81**. At 8.1s the dialog panel pops and types "I forgot the tell they are a lot of and has weapons sorry" (hold to the end).
Sequential/interaction: yes — four goblin waves on consecutive beats; counter rolls; typed line. Dialogue text holds ≥ 2s after typing.
Audio intent: dawning dread, played for laughs.
Audio-coupled idea: a drop per wave, the biggest wave gets a soft thud.
Music: full beat.
Transition mood: whip smear (horizontal blur) → Scene 3

### Scene 3 — The business — 4.5s (11.52-16.02)
Town at golden hour, close-up of the weaponsmith's stand. Shop panel already open: `Gold: 50 / > Skeleton Chip - 50g / Leave`. At 12.52s (beat) the purchase confirms: header becomes `Gold: 0 - Thanks!`, the gold HUD ticks 50 → 0, coin chips. At 13.52s the camera whip-pans across the map to the skeleton hideout (boss plus three skeletons around the red carpet). The boss line types: **"But its pricey like 50 golds"** with the HUD gold still at `0`.
Sequential/interaction: yes — purchase confirm and HUD count-down; whip pan on the beat; typed line (hold ≥ 1.5s).
Audio intent: wry. A coin sound, a quick swish, ticks.
Audio-coupled idea: coin chips at the HUD count-down; swish with the pan.
Music: full beat.
Transition mood: flash cut on the strong cue (16.02s) → Scene 4

### Scene 4 — Outro / title — 3.98s (16.02-20.00)
Town at dusk (twilight tint), the player and an unconfigured goblin NPC by the purple house. **GoblinQuests** slams in on the 16.02s strong cue (white flash, tiny scale settle, golden halo behind the title breathing with the bass), then stays dead still. Small line "made with libGDX" at 16.7s. At 17.02s (strong cue) the dialog panel pops and types "Developer forgot the set my identity :)", holding to the end. Gentle fade to black over the last 0.35s while the music fades.
Sequential/interaction: yes — title slam, then typed line.
Audio intent: a warm payoff, then quiet.
Audio-coupled idea: bell + thud on the slam; panel drop on 17.02s.
Music: strongest section, fading out over the last 1.6s.
Transition mood: clean fade to black.

**Music mood for this video:** upbeat
**Audio summary:** a quiet intro under the typed hook, the beat kicks in as the dialogue finishes, goblin waves and menu clicks land on the grid, the title slams on the track's strongest cue, and everything fades out after the final joke.
