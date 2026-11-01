# Prism Jerboa - animation inventory (GeckoLib `animation.json`, format 1.8.0)
Animation ids are `animation.prism_jerboa.<name>`. Rotations/positions are authored in Blockbench-Bedrock JSON convention
(+X rotation pitches the front down; legs swing back). **Sign conventions were not verified in a running GeckoLib renderer.**

| # | Name | Length | Loop | Content |
|---|---|---|---|---|
| 1 | idle | 4.0 s | loop | breathing (body scale), blink @3.0 s, ear twitches @1.3/2.9 s, ear-tip flop, whisker + nose twitch, tail wave, tail-ring spin |
| 2 | walk | 1.0 s | loop | alternating cautious steps (thigh/shin/foot), body bob, head counter-motion, arm swing, tail sway |
| 3 | run | 0.6 s | loop | paired hopping, crouch/extend squash, ears streaming, tail streaming, ring spin |
| 4 | giant_jump | 2.0 s | once | crouch + ear fold (0-0.55), launch/stretch (0.55-0.75), apex 22 px (1.05-1.2), descent prep, landing squash (1.58-1.85), recovery |
| 5 | sit | 3.0 s | loop | upright seated pose, ears raised, breathing, alternating ear flicks, blink |
| 6 | sleep | 5.0 s | loop | curled body, head down, ears folded over face, eyes closed, slow breathing, tail curled |
| 7 | crystal_chime_attack | 2.4 s | once | wind-up (0-0.6), horn glow growth, strike @0.9, 3 expanding amber rings (`wave_1..3`), follow-through, recovery |
| 8 | prism_shield | 4.0 s | once | brace pose, shield shards form/spin (`shield_fx`, `shield_fx_2` counter-rotate), hold, dissolve |
| 9 | summoned_arrival | 3.0 s | once | sand swirl (`swirl_fx`) grows, jerboa rises, hops cheerfully, landing squash, ear wiggle/head tilt |
| 10 | death | 2.2 s | hold_on_last_frame | gentle collapse onto its side, ears droop, eyes close, horns dim, tail goes limp |

FX bones (`chime_fx`, `shield_fx`, `shield_fx_2`, `swirl_fx`) are scaled to 0 at t=0 in every animation that does not use them.
If a state-machine blend shows them briefly, hide them from the renderer too.
Recommended renderer scale 0.5 (model is ~1.7 blocks tall because of the ears); hitbox ~0.5 x 0.9.
Glow mask: `Prism_Jerboa_Glow_Mask.png` (white, alpha = intensity) for an emissive layer.
