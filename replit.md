# MGZ - Minecraft Spigot Plugin

## Overview
MGZ is a comprehensive Minecraft Spigot plugin for version 1.20.1+ that provides server utilities, region protection, and administrative tools. This plugin enhances server management with features like auto-save, auto-restart, inventory backups, and a custom region protection system.

**Current State**: Successfully built and ready for deployment to a Minecraft Spigot server (version 1.20.1).

## Recent Changes
- **2025-11-13**: Initial setup in Replit environment
  - Installed Java toolchain (GraalVM 22.3)
  - Installed Gradle build system
  - Configured build workflow
  - Successfully compiled plugin JAR (MGZ-1.0.0.jar, 14MB)

## Project Architecture

### Technology Stack
- **Language**: Java 17
- **Build System**: Gradle 8.14.2
- **Target Platform**: Spigot API 1.20.1-R0.1-SNAPSHOT
- **Dependencies**:
  - Apache Commons Lang3 3.12.0
  - SQLite JDBC 3.43.0.0
  - UltimateChat 1.9.2-b303 (external plugin dependency)

### Key Features
1. **Region Protection System**
   - Custom region definition with wand selection tool
   - Configurable flags: PVP, MOB_SPAWN, BUILD, BREAK, INTERACT, INVINCIBLE, FIRE_SPREAD, EXPLOSION, ITEM_DROP, ITEM_PICKUP
   - Priority-based region overlapping
   - Item blocking in regions (hotbar and offhand protection)

2. **Player Data Backup System**
   - Complete player data backup (inventory, ender chest, XP, health, hunger, effects, location, game mode)
   - Memory-cached with debounced disk writes for performance
   - Automatic backups on join, quit, and death
   - Crash recovery system
   - Admin command: `/playerdata`

3. **Auto-Save System**
   - Configurable interval-based world saving
   - Broadcast notifications
   - Manual trigger via `/autosave`

4. **Auto-Restart System**
   - Scheduled server restarts at configured times
   - Countdown warnings
   - Custom kick messages
   - Manual trigger via `/autorestart`

5. **Server Utilities**
   - Random teleport (RTP) with configurable boundaries
   - Chat management (clear, announcements)
   - Light control
   - Item repair
   - MOTD customization
   - Weather control (disable rain)
   - GameRule management across worlds
   - Sign shop blocking
   - Armor stand placement control

### Project Structure
```
MGZ/
├── src/main/java/mglucas0123/
│   ├── Principal.java              # Main plugin class
│   ├── commands/                   # Command implementations
│   ├── events/                     # Event listeners
│   ├── config/                     # Configuration GUI system
│   │   ├── editor/                 # Interactive config editor
│   │   └── menus/                  # GUI menus
│   ├── autosave/                   # Auto-save and auto-restart managers
│   ├── inventory/                  # Player data backup system
│   └── region/                     # Region protection system
├── src/main/resources/
│   ├── config.yml                  # Plugin configuration
│   └── plugin.yml                  # Plugin metadata
├── libs/
│   └── UltimateChat-1.9.2-b303-Universal.jar
├── build.gradle                    # Gradle build configuration
└── build/libs/
    └── MGZ-1.0.0.jar              # Compiled plugin (output)
```

## Building the Plugin

### Automatic Build
The "Build Plugin" workflow is configured to automatically compile the plugin JAR file.
Simply run the workflow and the compiled JAR will be available at `build/libs/MGZ-1.0.0.jar`.

### Manual Build
```bash
gradle build --no-daemon
```

### Output
The compiled plugin JAR file: `build/libs/MGZ-1.0.0.jar`

## Deployment Instructions

This plugin is designed to run on a Minecraft Spigot/Paper server, **not directly in Replit**.

### To Deploy:
1. Build the plugin using the workflow or manual build command
2. Download the `MGZ-1.0.0.jar` file from `build/libs/`
3. Upload it to your Minecraft server's `plugins/` directory
4. Restart your Minecraft server
5. Configure the plugin by editing `plugins/MGZ/config.yml`

### First-Time Setup:
1. On first load, the plugin will generate a default `config.yml`
2. Use the in-game command `/mgzconfig` to open the interactive configuration editor
3. Configure regions using `/region wand` to get the selection wand
4. Set up auto-save and auto-restart schedules as needed

## Commands Reference

### Region Commands
- `/region wand` - Get region selection wand
- `/region create <name>` - Create a region
- `/region delete <name>` - Delete a region
- `/region flag <region> <flag> <true/false>` - Set region flags
- `/region info [region]` - Show region information
- `/region list` - List all regions
- `/region priority <region> <priority>` - Set region priority

### Administrative Commands
- `/mgzconfig` - Open interactive config editor
- `/mgzreload` - Reload plugin configuration
- `/playerdata <list|restore|backup|info> [player] [number]` - Manage player data backups
- `/autosave <now|reload|info>` - Control auto-save system
- `/autorestart <now|countdown|reload|info>` - Control auto-restart system

### Utility Commands
- `/anuncio <message>` - Broadcast announcement
- `/limparchat` - Clear chat
- `/luz` - Control light level
- `/repair` - Repair held item
- `/rtp` - Random teleport

## Permissions
- `mgz.config` - Access to config editor
- `mgz.playerdata` - Access to player data backups
- `mgz.autosave` - Control auto-save system
- `mgz.autorestart` - Control auto-restart system
- `mgz.region.bypass` - Bypass region protection

## Database
The plugin uses SQLite for region storage and player data backups. Database files are stored in:
- `plugins/MGZ/regions.db` - Region protection data
- `plugins/MGZ/player_backups.db` - Player data backups

## Notes
- This is a server-side plugin and requires a Minecraft Spigot/Paper server to run
- The plugin is written in Portuguese (Brazilian)
- Java 17+ is required for compilation
- Compatible with Minecraft 1.20.1+
- Dependencies are bundled into the final JAR file

## Author
- Developer: Mglucas0123
- Discord: Mglucas0123#9371
- Version: 1.0.0
