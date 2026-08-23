<div align="center">

# Spearfix

Server-side spear fixes for Minecraft 26.1.2 (Fabric).

[![English](https://img.shields.io/badge/README-English-2ea44f?style=for-the-badge)](./README.md)
[![简体中文](https://img.shields.io/badge/README-%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87-d73a4a?style=for-the-badge)](./README_zh-CN.md)

</div>

Spearfix is an independent, **server-side only** implementation fixing two vanilla spear bugs. It requires no client mod and no Fabric API, and declares `"environment": "server"` — if placed in a client `mods/` folder it simply won't load.

## What it fixes

- **MC-310857 — hitting fast targets.** Remote entities render up to 3 ticks behind their true position, but the vanilla hit scan only tests live bounding boxes, so you have to lead your shots. Spearfix re-runs the scan against entity positions rewound by the interpolation lag. The mechanism is purely server-side: no client reporting, no extra packets.
- **MC-310858 — charge cooldown burned on misses.** Vanilla locks a target into the contact cooldown before the charge sub-checks run, so a tick that lands nothing still burns the cooldown. Spearfix drops the early lock and only applies it when an attack actually connects.

## Requirements

- Minecraft 26.1.2
- Fabric Loader 0.19.3+
- Java 25

No Fabric API needed.

## Install

Drop the jar into `mods/` on the server. Clients do not need the mod.

## Configuration

`config/spearfix.toml` is created on first launch:

| Key | Default | Meaning |
| --- | --- | --- |
| `rewindTicks` | `3` | How many ticks the scan rewinds entity positions. `0` disables the feature. |
| `refundMissedCooldown` | `true` | Cooldown only locks when a charge hit actually connects. `false` = vanilla. |

## Building

JDK 25, then `gradlew build`. Output is in `build/libs/`.

## Acknowledgements

Thanks to the SpearAim community for drawing attention to these vanilla bugs. Spearfix shares no code with any other mod — the mechanisms here (rewind-compensated scan, cooldown refund) are its own implementation.

## License

[MIT](LICENSE)
