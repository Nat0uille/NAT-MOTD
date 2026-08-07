![GitHub Actions](https://img.shields.io/github/actions/workflow/status/Nat0uille/NAT-MOTD/main.yml?style=for-the-badge)
![License](https://img.shields.io/github/license/Nat0uille/NAT-MOTD?style=for-the-badge)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/nat-motd?style=for-the-badge&label=MODRINTH%20DOWNLOADS)
![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/nat-motd?style=for-the-badge)

![NAT-MOTD Banner](https://cdn.modrinth.com/data/cached_images/1881385b1f211cdf69a117b960734923d5e2412f_0.webp)
## What is ?

Nat-MOTD is an advanced and flexible Message of the Day (MOTD) management plugin for Minecraft servers and proxies. It allows administrators to dynamically customize the server list MOTD, supporting multiple MOTDs, centering, custom icons, live placeholders, and per-MOTD max player settings. Available for **Paper** and **Velocity**.

## Features

- Create unlimited MOTDs and select which one to display via the config, or with `/nat-motd select <number>`
- Customize each MOTD line using MiniMessage format
- Automatically center MOTD lines if desired
- Set a custom icon (favicon) per MOTD
- Set a different max player count for each MOTD, or show `(online players + 1)` so the server never appears full
- Automatically select a MOTD based on the hostname the player connected with (multi-domain setups)
- Custom hover text over the player count in the server list
- Named countdown timers you can drop into any line
- Live placeholders: `{version}`, `{online}`, `{max}`, `{timer:<name>}`
- Automatic update checks against Modrinth, with auto-download of new versions (installed automatically on Paper; downloaded for manual install on Velocity)

## Commands

`/nat-motd` (alias: `/motd`)

| Command | Description |
| --- | --- |
| `/nat-motd reload` | Reloads the plugin configuration |
| `/nat-motd select <number>` | Selects which MOTD to use |

## Permissions

| Permission | Description |
| --- | --- |
| `natmotd.reload` | Allows reloading the configuration |
| `natmotd.select` | Allows selecting which MOTD to use |
| `natmotd.admin` | Grants all Nat-MOTD permissions |
| `natmotd.*` | Grants all plugin permissions (same as natmotd.admin) |
