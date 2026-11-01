# 02 — Configuration Schema

Suggested Forge config type: **SERVER** config (world-synced, authoritative; file `serverconfig/<modid>-autopilot.toml`) for gameplay/performance values, plus an optional **CLIENT** config for HUD placement. Use Forge's config spec API the mod already uses (check how existing configs are registered). Names below are proposals.

Files: `x4_autopilot-server.toml.example` (annotated), `x4_autopilot.schema.json` (machine-readable, JSON Schema draft 2020-12).

Validation rules: every numeric value has a min/max (clamp, log a warning once); booleans default as listed; unknown keys ignored; changing values must apply without restart where practical (read at the next interval).

Key groups: `general`, `smartFollow`, `antiStuck`, `buildingMode`, `combatAssist`, `performance`, `status`, `multiplayer`. See the TOML for every key, default, and range.
