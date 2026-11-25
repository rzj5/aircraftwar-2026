# 飞机大战 AircraftWar2026 —— 从 Windows 到 Android 的移植过程

本仓库用于完整记录《移动应用开发》课程实验的**逐轮迭代过程**：不仅保存最终代码，也保存每一轮的代码改动、改动原因与实测结果，便于老师按提交历史逐条对照检查。

## 仓库结构

| 目录 | 内容 |
| --- | --- |
| `desktop/` | 第 0 轮基线：原始 Windows 桌面版（Java Swing + Timer 游戏循环），即迁移来源 |
| `android/` | Android 工程 `AircraftWar2026`（Java，minSdk 30），逐轮迭代的成果 |
| `docs/` | 移植与实测报告、开题任务书（Word） |
| `docs/evidence/` | 关键实测截图（模拟器内真机流程证据） |
| `ITERATIONS.md` | **逐轮迭代说明**：每轮改了什么、为什么改、实测结果如何 |

## 迭代主线

0. 基线：桌面版源码入库（可编译、可玩的原始版本）
1. step1 空壳先跑起来：Gradle 工程 + Manifest + 布局 + Activity 骨架
2. step2 资源与图片：资源改名进 `res/drawable`、`res/raw`，`ImageIO.read` → `BitmapFactory.decodeResource`
3. step3 游戏循环：`BaseGame` 改 `SurfaceView` + 子线程 `lockCanvas/unlockCanvasAndPost`
4. step4 交互与逻辑：触屏拖动替代鼠标、碰撞计分接回循环、`Handler` 回主线程弹窗存成绩
5. step5 音频与排行榜：`music` 包 `MyMediaPlayer`/`MySoundPool`，分难度排行榜持久化
6. fix 困难模式超级 Boss 血量倍率（迁移漏接的调用点）

## 如何编译运行

```bash
# 需要 JDK 17（含 jlink）、Android SDK（platform 34、build-tools 34.0.0、platform-tools）、Gradle 8.7
cd android
echo "sdk.dir=<你的 Android SDK 路径>" > local.properties
gradle assembleDebug            # 产物：app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n edu.hitsz.aircraftwar/.MainActivity
```

桌面版（第 0 轮基线）用 IntelliJ IDEA 直接打开 `desktop/` 编译运行，或用 JDK 11+ 命令行：

```bash
cd desktop && javac -encoding UTF-8 -d build $(find src -name "*.java") && java -cp build edu.hitsz.application.Main
```

> 说明：桌面版通过相对路径读取 `src/images/`、`src/videos/`，因此**必须在 `desktop/` 目录下启动**。

## 提交约定

- 每轮迭代的**代码与 `ITERATIONS.md` 说明同批提交**，不事后补写。
- 提交信息统一为 `第N轮 <阶段名>：<本轮做了什么>`，正文列出改动文件与验证方式。
- 不含任何访问凭据、本机绝对路径配置（`local.properties`、`.idea/`）与构建产物，规则见 `.gitignore`。
