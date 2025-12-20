# Minecraft Minesweeper

![MIT License](https://img.shields.io/badge/License-MIT-a6da95?style=for-the-badge&labelColor=363a4f)
![Version mms-1.0](https://img.shields.io/badge/Version-mms--1.0-f5a97f?style=for-the-badge&labelColor=363a4f)
![Maven Version 1.0.0](https://img.shields.io/badge/Maven-1.0.0-91d7e3?style=for-the-badge&labelColor=363a4f)
[![Uses Fooble-PaperMC-Utils](https://img.shields.io/badge/Uses-Fooble--PaperMC--Utils-eed49f?style=for-the-badge&labelColor=363a4f)](https://github.com/fooble2137/Fooble-Paper-Utils)

A plugin for Minecraft PaperMC 1.21.8 which brings the classic Minesweeper game to your Minecraft server. Players can
enjoy the thrill of uncovering hidden mines while navigating a grid of blocks.

## Usage

### Installation

1. Download the latest version of the Minecraft Minesweeper plugin from
   the [Releases](https://github.com/fooble2137/minecraft-minesweeper/releases) page.
2. Put the downloaded `.jar` file into the `plugins` directory of your PaperMC server.
3. Restart your server to load the plugin. It is **not** recommended to use the command `/reload`.

### Commands

- `/minesweeper` - Configure and start a new game.
- `/minesweeper exit` - Exit the current Minesweeper game.

### Game Controls

- **Left Click** - Reveal a block. If it's a mine, you lose the game!
- **Right Click** - Mark a block as a mine by flagging or unflagging it.

## Planned Features

- [x] Multiple games at once
- [ ] Configuration file *(especially with regard to block types and messages)*
- [ ] Better scoreboard
- [ ] Timer and high scores

## Bugs and Issues

If you encounter any bugs or issues, please report them on
the [Issues](https://github.com/fooble2137/minecraft-minesweeper/issues) page of this repository.
