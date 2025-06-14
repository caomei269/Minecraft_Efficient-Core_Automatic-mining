# 📖 ChunkOre AutoMiner

[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.4-green.svg)](https://minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.97.2-orange.svg)](https://fabricmc.net/)

An efficient Minecraft Fabric mod that provides automated mining and logging functionality.

English | [中文](README_CN.md)

## ✨ Features

- 🔨 **Automated Mining** - Intelligently scan and mine ores within the chunk
- 🌲 **Automated Logging** - Automatically chop down and collect logs in the chunk
- ⚡ **Redstone Control** - Control on/off with redstone signals
- 📦 **Item Storage** - Built-in storage system with automatic item collection
- 🔧 **Tool Management** - Support for multiple tools acceleration and auto-replacement
- 🎯 **Smart Scanning** - 5-second chunk scanning with precise target location

## 🚀 Quick Start

### Prerequisites

- Minecraft 1.20.4
- Fabric Loader 0.16.14+
- Fabric API 0.97.2+

### Installation

1. Download the latest mod file
2. Place the `.jar` file in your `mods` folder
3. Launch the game

## 📋 Usage Guide

### Core Blocks

#### 🔨 Mining Core
- Automatically mines surrounding ores
- Supports all vanilla ore types
- Configurable mining range and efficiency

#### 🌲 Logging Core
- Automatically cuts surrounding logs
- Supports all vanilla wood types
- Intelligently recognizes complete tree structures

### Crafting Recipes

#### Mining Core
```
[Iron Block] [Iron Block] [Iron Block]
[Iron Block] [Iron Pickaxe] [Iron Block]
[Iron Block] [Redstone Block] [Iron Block]
```

#### Logging Core
```
[Iron Block] [Log] [Iron Block]
[Log] [Iron Axe] [Log]
[Iron Block] [Redstone Block] [Iron Block]
```

### How to Use

1. **Place Core Block** - Place the core block in a suitable location
2. **Right-click to Open GUI** - Configure tools and storage
3. **Connect Redstone** - Provide redstone signal to activate the core
4. **Wait for Scanning** - Automatically starts working after 5 seconds

### Interface Description

**Mining Core Interface:**
- 🎒 **Ore Storage Area** (3×3) - Store mined ores
- ⛏️ **Main Pickaxe Slot** - Place the primary pickaxe
- ⚡ **Acceleration Pickaxe Area** (3×3) - Additional pickaxes for efficiency

**Logging Core Interface:**
- 🎒 **Log Storage Area** (3×3) - Store cut logs
- 🪓 **Main Axe Slot** - Place the primary axe
- ⚡ **Acceleration Axe Area** (3×3) - Additional axes for efficiency

## 🔧 Development

### Building the Project

```bash
# Clone the repository
git clone https://github.com/username/ChunkOreAutoMiner.git
cd ChunkOreAutoMiner

# Build the mod
./gradlew build

# Run client for testing
./gradlew runClient

# Run server for testing
./gradlew runServer
```

### Development Environment

- Java 17+
- IntelliJ IDEA or Eclipse
- Fabric Development Kit

### Project Structure

```
src/
├── main/
│   ├── java/com/chunkore/autominer/
│   │   ├── blocks/          # Block definitions
│   │   ├── blockentity/     # Block entities
│   │   ├── screen/          # GUI interfaces
│   │   ├── util/            # Utility classes
│   │   └── ChunkOreAutoMiner.java
│   └── resources/
│       ├── assets/          # Client-side resources
│       ├── data/            # Data packs
│       └── fabric.mod.json
└── client/
    └── java/                # Client-only code
```

## 🤝 Contributing

We welcome all forms of contributions!

### How to Contribute

1. Fork this repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Contribution Guidelines

- Follow existing code style
- Add appropriate comments and documentation
- Ensure all tests pass
- Update relevant documentation

### Reporting Issues

If you find a bug or have a feature suggestion:

1. Check if there's already a related issue
2. Create a new issue with detailed information
3. Include reproduction steps and environment info

## 🐛 Known Issues

- [ ] UI text layout overlaps in English interface
- [ ] Some mod compatibility issues
- [ ] Large-scale scanning may affect performance

## 📝 Changelog

### v1.0.0 (2024-06-14)
- ✨ Initial release
- 🔨 Added Mining Core functionality
- 🌲 Added Logging Core functionality
- 📦 Basic GUI interface
- ⚡ Redstone control system

## 📄 License

This project is licensed under the GPLv3 License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Fabric](https://fabricmc.net/) - Modding framework
- [Minecraft](https://minecraft.net/) - The game itself
- All contributors and testers

## 📞 Contact
- Bug Reports: [Issues]([https://github.com/username/ChunkOreAutoMiner/issues](https://github.com/caomei269/Minecraft_Efficient-Core_Automatic-mining/issues))


---

⭐ If this project helps you, please give us a Star!
