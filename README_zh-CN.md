<div align="center">

<img src="https://raw.githubusercontent.com/H2OLCCL/Spearfix/main/logo.png" alt="Spearfix logo" width="128" />

# Spearfix

面向 Minecraft 1.21.11+ 的服务端长矛修复（Fabric）。

[![English](https://img.shields.io/badge/README-English-2ea44f?style=for-the-badge)](./README.md)
[![简体中文](https://img.shields.io/badge/README-%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87-d73a4a?style=for-the-badge)](./README_zh-CN.md)
[![Modrinth](https://img.shields.io/badge/Modrinth-spearfix-1bd96a?style=for-the-badge&logo=modrinth&logoColor=white)](https://modrinth.com/mod/sparefix)

</div>

Spearfix 是一个独立实现的长矛 bug 修复 Mod。所有逻辑都在服务端运行，但 jar 放哪边都能用：

- **独立服务器**：放进服务端的 `mods/` 即可，客户端无需安装。
- **单机 / 局域网**：放进你自己客户端的 `mods/`，通过内置服务器生效。

不需要 Fabric API。

## 修了什么

- **MC-310857 — 打高速目标费劲。** 远端实体渲染位置最多落后真实位置 3 tick，而原版命中扫描只检测实时碰撞箱，所以必须打提前量。Spearfix 会把实体位置按插值滞后回退后再扫一遍。机制完全在服务端：没有客户端上报，没有额外数据包。
- **MC-310858 — 戳空了也烧冷却。** 原版在冲锋子判定之前就把目标锁进接触冷却，没打中也浪费一个 tick。Spearfix 去掉提前锁定，只在攻击真正命中时才上锁。

## 环境要求

- Fabric Loader 0.19.3+
- 不需要 Fabric API。

## 支持的版本

| Minecraft | 分支 | Java |
| --- | --- | --- |
| 1.21.11 | `ver/1.21.11` | 21 |
| 26.1 | `ver/26.1` | 25 |
| 26.1.1 | `ver/26.1.1` | 25 |
| 26.1.2 | `main` | 25 |
| 26.2 | `ver/26.2` | 25 |

每个分支构建出 `spearfix-0.2.0+<版本>.jar`。各分支源码几乎完全一致，只有构建配置（Minecraft 版本、Java 目标版本、ModMenu 版本，以及 1.21.11 所用的重映射插件与旧版界面 API）不同。

## 安装

- 多人服务器：放进**服务端**的 `mods/`。
- 单机 / 局域网：放进**你自己客户端**的 `mods/`（通过内置服务器生效）。
- 双端都放也可以，不会有冲突。

## 配置

首次启动会生成 `config/spearfix.toml`：

| 键 | 默认值 | 含义 |
| --- | --- | --- |
| `rewindTicks` | `3` | 扫描时回退实体位置的 tick 数，`0` 关闭此功能 |
| `refundMissedCooldown` | `true` | 冷却只在冲锋真正命中时上锁，`false` = 原版行为 |

安装 [ModMenu](https://modrinth.com/mod/modmenu) 后，可以在游戏内的模组列表（Mods → Spearfix）直接改这两个值。单机下改动立即生效；独立服务器请在服务端的配置文件里修改。配置界面跟随游戏语言，而游戏语言会按你的地区自动选择——内置简体中文与英文。

## 构建

装 JDK 25，跑 `gradlew build`，产物在 `build/libs/`。

## 许可证

[MIT](LICENSE)
