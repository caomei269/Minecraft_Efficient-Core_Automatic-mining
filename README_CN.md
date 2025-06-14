# 📖 高效核心_自动挖掘

[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.4-green.svg)](https://minecraft.net/)
[![Fabric](https://img.shields.io/badge/Fabric-0.97.2-orange.svg)](https://fabricmc.net/)

一个高效的 Minecraft Fabric 模组，提供自动化挖矿和砍树功能。

[English](README.md) | 中文

## ✨ 特性

- 🔨 **自动挖矿** - 智能扫描并挖掘区块内的矿物并收集
- 🌲 **自动砍树** - 自动砍伐区块内的原木并收集
- ⚡ **红石控制** - 通过红石信号控制开关
- 📦 **物品存储** - 内置存储系统，自动收集物品
- 🔧 **工具管理** - 支持多工具加速和自动更换
- 🎯 **智能扫描** - 5秒扫描区块，精确定位目标

## 🚀 快速开始

### 前置要求

- Minecraft 1.20.4
- Fabric Loader 0.16.14+
- Fabric API 0.97.2+

### 安装

1. 下载最新版本的模组文件 [CurseForge](https://curseforge.com/minecraft/mc-mods/efficient-core-automatic-mining)
2. 将 `.jar` 文件放入 `mods` 文件夹
3. 启动游戏

## 📋 使用指南

### 核心方块

#### 🔨 挖矿核心
- 自动挖掘周围的矿物
- 支持所有原版矿物类型
- 可配置挖掘范围和效率

#### 🌲 砍树核心
- 自动砍伐周围的原木
- 支持所有原版木材类型
- 智能识别完整树木结构

### 合成配方

#### 挖矿核心
```
[铁块] [铁块] [铁块]
[铁块] [铁镐] [铁块]
[铁块] [红石块] [铁块]
```

#### 砍树核心
```
[铁块] [原木] [铁块]
[原木] [铁斧] [原木]
[铁块] [红石块] [铁块]
```

### 使用方法

1. **放置核心方块** - 在合适的位置放置核心方块
2. **右键打开界面** - 配置工具和存储
3. **连接红石** - 提供红石信号激活核心
4. **等待扫描** - 5秒后自动开始工作

### 界面说明

**挖矿核心界面：**
- 🎒 **矿物存储区** (3×3) - 存储挖掘到的矿物
- ⛏️ **主镐子槽** - 放置主要使用的镐子
- ⚡ **加速镐子区** (3×3) - 额外镐子提高效率

**砍树核心界面：**
- 🎒 **原木存储区** (3×3) - 存储砍伐的原木
- 🪓 **主斧子槽** - 放置主要使用的斧子
- ⚡ **加速斧子区** (3×3) - 额外斧子提高效率

## 🔧 开发

### 构建项目

```bash
# 克隆项目
git clone https://github.com/username/ChunkOreAutoMiner.git
cd ChunkOreAutoMiner

# 构建模组
./gradlew build

# 运行客户端测试
./gradlew runClient

# 运行服务端测试
./gradlew runServer
```

### 开发环境

- Java 17+
- IntelliJ IDEA 或 Eclipse
- Fabric 开发工具包

### 项目结构

```
src/
├── main/
│   ├── java/com/chunkore/autominer/
│   │   ├── blocks/          # 方块定义
│   │   ├── blockentity/     # 方块实体
│   │   ├── screen/          # GUI界面
│   │   ├── util/            # 工具类
│   │   └── ChunkOreAutoMiner.java
│   └── resources/
│       ├── assets/          # 客户端资源
│       ├── data/            # 数据包
│       └── fabric.mod.json
└── client/
    └── java/                # 客户端专用代码
```

## 🤝 贡献

我们欢迎所有形式的贡献！

### 如何贡献

1. Fork 这个项目
2. 创建你的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交你的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开一个 Pull Request

### 贡献指南

- 遵循现有的代码风格
- 添加适当的注释和文档
- 确保所有测试通过
- 更新相关文档

### 报告问题

如果你发现了 bug 或有功能建议，请：

1. 检查是否已有相关 issue
2. 创建新的 issue 并提供详细信息
3. 包含复现步骤和环境信息

## 🐛 已知问题

- [ ] 英文界面下UI文字排版重叠
- [ ] 某些模组兼容性问题
- [ ] 大范围扫描时可能影响性能

## 📝 更新日志

### v1.0.0 (2024-06-14)
- ✨ 初始版本发布
- 🔨 添加挖矿核心功能
- 🌲 添加砍树核心功能
- 📦 基础GUI界面
- ⚡ 红石控制系统

## 📄 许可证

本项目采用 GPLv3 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [Fabric](https://fabricmc.net/) - 模组开发框架
- [Minecraft](https://minecraft.net/) - 游戏本体
- 所有贡献者和测试者

## 📞 联系我们
- 问题反馈：[Issues](https://github.com/caomei269/Minecraft_Efficient-Core_Automatic-mining/issues))


---

⭐ 如果这个项目对你有帮助，请给我们一个 Star！
