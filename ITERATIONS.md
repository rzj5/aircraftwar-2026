# 迭代说明（ITERATIONS）

按时间顺序记录每一轮迭代：**改了什么 / 为什么改 / 实测结果**。每轮的代码改动与本文件对应条目在同一提交里入库。

实测环境（后续各轮共用，不再重复）：
- 主机 Windows，Android SDK 置于 `C:\androidsdk`（platform-tools、platforms android-30/34、build-tools 34.0.0、emulator、`system-images;android-30;google_apis;x86_64`）。
- 构建：JDK 17（`jdk-17.0.2`，含 `jlink`）+ Gradle 8.7，`gradle --no-daemon assembleDebug`。
- 运行：Android Emulator 37.3.10.0，AVD `aw30`（Android 11 / API 30 / 1080×2340），`-gpu swiftshader_indirect` 软件渲染。

---

## 第 0 轮　基线：Windows 桌面版源码入库

**改动内容**
- 新建仓库，收录原始桌面版：`desktop/src`（`edu.hitsz` 包下 aircraft/application/audio/basic/bullet/dao/entity/prop/strategy/ui 共 3521 行）、`desktop/test`、`desktop/uml`、三个难度成绩文件、IntelliJ 模块文件 `desktop/AircraftWar-base.iml`。
- 新增 `README.md`、`.gitignore`（排除 `build/`、`out/`、`local.properties`、`.idea/`、`*.apk`、`*.class` 等）。

**改动原因**
- 迁移必须有可对照的"迁移前"版本。桌面版是课程的原始工程，先原样入库，后续每轮 Android 实现都能与它逐类对照。
- 该版本此前已在主机上实际编译运行通过（`javac` + `java -cp build edu.hitsz.application.Main`），并修过 3 个启动缺陷（UI Designer 空桩方法、音频资源不在 classpath、`mark/reset` 不支持），因此作为可信基线。

**实测结果**
- 桌面版可正常启动、进入游戏并完成一局：菜单三档难度 → 游戏内 `SCORE:0 / LIFE:100` → 敌机生成与自动射击 → 一局结束弹「游戏结束！输入姓名：」→ 成绩写入对应难度 txt → 排行榜显示 → 返回主菜单可重开。
- 该轮只做代码入库，未改动任何桌面版功能。
