![GitHub Actions](https://img.shields.io/github/actions/workflow/status/Nat0uille/NAT-MOTD/main.yml?style=for-the-badge)
![License](https://img.shields.io/github/license/Nat0uille/NAT-MOTD?style=for-the-badge)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/nat-motd?style=for-the-badge&label=MODRINTH%20DOWNLOADS)
![Modrinth Game Versions](https://img.shields.io/modrinth/game-versions/nat-motd?style=for-the-badge)

![NAT-MOTD Banner](https://cdn.modrinth.com/data/cached_images/1881385b1f211cdf69a117b960734923d5e2412f_0.webp)
## What is ?

Nat-MOTD is an advanced and flexible Message of the Day (MOTD) management plugin for Minecraft servers. It allows administrators to dynamically customize the server list MOTD, supporting multiple MOTDs, centering, and per-MOTD max player settings.

## Features

- Create unlimited MOTDs and select which one to display via the config
- Customize each MOTD line using MiniMessage format
- Automatically center MOTD lines if desired
- Set a different max player count for each MOTD

## Commands

| Command                        | Description                                 |
|--------------------------------|---------------------------------------------|
| `/natmotd reload`              | Reloads the plugin configuration            |
| `/natmotd select <number>`     | Selects which MOTD to use                   |

## Permissions

| Permission         | Description                                          |
|--------------------|------------------------------------------------------|
| `natmotd.reload`   | Allows reloading the configuration                   |
| `natmotd.select`   | Allows selecting which MOTD to use                   |
| `natmotd.admin`    | Grants all Nat-MOTD permissions                      |
| `natmotd.*`        | Grants all plugin permissions (same of natmotd.admin)|
