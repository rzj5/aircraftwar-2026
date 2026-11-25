# 迭代说明（ITERATIONS）

按时间顺序记录每一轮迭代：**改了什么 / 为什么改 / 实测结果**。每轮的代码改动与本文件对应条目在同一提交里入库。

实测环境（后续各轮共用，不再重复）：
- 主机 Windows；Android SDK 置于**不含中文的路径**（写作时用 `C:/androidsdk`，可换成任意 ASCII 路径），已装 platform-tools、platforms android-30/34、build-tools 34.0.0、emulator、`system-images;android-30;google_apis;x86_64`。复现时请把下文命令里的 SDK 路径替换为你自己的。
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

---

## 第 1 轮　Android 移植完成：从空壳到可玩

按 PPT《实验2》p5 的四步迁移路线推进，每步走完即提交，本轮的五个子步骤对应原工程的 5 次中间提交（step1–step5）。

**改动内容（本仓库落地的最终树）**
- 新增独立 Android 工程 `android/`：`settings.gradle`、项目级与模块级 `build.gradle`（AGP 8.5.2 / namespace `edu.hitsz.aircraftwar` / `compileSdk 34` / **`minSdk 30`** / Java 17）、`gradle.properties`、`AndroidManifest.xml`（MainActivity 为 LAUNCHER、GameActivity、RankActivity 三个 `<activity>`，全部 portrait）。
- 新增布局与主题：`res/layout/activity_main.xml`（标题 + 三档难度按钮 `button_easy/normal/hard` + `button_rank` + `switch_music`）、`activity_rank.xml`（`rank_title` / `rank_list` / `button_back`）、`res/values/strings.xml`、`themes.xml`。
- 资源：`res/drawable` 16 张图（**全部改小写**：`elitePlus.png→elite_plus.png`、`elitePro.png→elite_pro.png`、`prop_bulletPlus.png→prop_bullet_plus.png`、`bg2/3/4.jpg→bg_easy/normal/hard.jpg`）；`res/raw` 6 个音频（`bgm.wav`、`bgm_boss.wav`、`bullet_hit.wav`、`bomb_explosion.wav`、`get_supply.wav`、`game_over.wav`）。
- 纯逻辑类照搬（改包名即可）：`basic/AbstractFlyingObject`、`aircraft/*`（含 4 类敌机 + 超级 Boss + 5 个工厂 + 观察者接口）、`bullet/*`、`strategy/*`（单发/双发/扇形/环射/不开火）、`prop/*`（5 种道具 + 工厂）。
- 平台相关实现替换：
  - `Main`(JFrame/JPanel) → `MainActivity`（三种难度按钮各建 Intent 带 `extra_difficulty` 跳 `GameActivity`，实验3 p17 三步式页面跳转）。
  - `BaseGame extends JPanel + javax.swing.Timer` → `BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable`；`surfaceCreated` 起 `game-loop` 线程、`surfaceChanged` 取真实宽高算 `scale = min(w/512, h/768)` 与居中偏移、`surfaceDestroyed` 置 `running=false` 并 `join(1000)`；`run()` 内 `lockCanvas → 绘制 → unlockCanvasAndPost`，`TICK_INTERVAL = 40ms`。
  - `ImageManager`(`ImageIO.read` 相对路径) → `image/BitmapManager`（16 个静态 Bitmap，`load(Context)` 内 `if (loaded) return;` 一次性 `BitmapFactory.decodeResource`，用 `getApplicationContext()`）。
  - 鼠标拖拽(`HeroController.mouseDragged`) → `setOnTouchListener`，`ACTION_DOWN/MOVE` 里把屏幕坐标反算成 512×768 虚拟坐标并夹紧边界。
  - `AudioManager`(`AudioSystem`) → `music/MyMediaPlayer`（单例、`MediaPlayer.create` + `setLooping(true)`，同曲在播则早退）与 `music/MySoundPool`（`AudioAttributes` USAGE_GAME + `setMaxStreams(6)`，`load` 缓存 soundId）。
  - `JOptionPane.showInputDialog` → `BaseGame` 子线程结束时 `mainHandler.sendMessage(MSG_GAME_OVER, GameResult)`，由 `GameActivity` 在主线程弹 `AlertDialog + EditText`（实验3 p30-p32 的 Handler 用法）。
  - 成绩存储 `ScoreDaoImpl` 从「相对路径 txt」改为 `context.getFilesDir()/{difficulty}_score.txt`；时间字段用 `Date + SimpleDateFormat` 替代 `LocalDateTime`。
- 本轮实测中发现并修掉的三个问题：① 排行榜行文字在深色背景上不可见（`RankActivity` 动态 TextView 未设色）→ 加 `setTextColor(Color.WHITE)`；② BGM 开局不播放（`play()` 只在 Boss 相关处调用）→ `surfaceCreated` 开头补 `MyMediaPlayer.play(R.raw.bgm)`；③ 困难模式超级 Boss 血量倍率的方法没有调用点 → 见第 2 轮单独修复。

**改动原因**
- PPT 明确要求「纯逻辑照搬、平台实现替换」，保留原有类结构与调用关系才能让两版代码逐类对照。
- 资源必须全小写，否则 `R` 不生成、编译报 `Cannot resolve symbol R`。
- 绘制必须离开主线程，否则每帧绘制会把 UI 线程占满。

**实测结果（Android Emulator 37.3.10.0 / AVD `aw30` / API 30 / 1080×2340 / swiftshader 软件渲染）**
- 构建：`gradle --no-daemon assembleDebug` → `BUILD SUCCESSFUL`，产物 `app-debug.apk`（17,485,239 B）。
- 安装启动：`adb install -r` → `Success`；`am start` → `Displayed edu.hitsz.aircraftwar/.MainActivity: +1s27ms`，菜单三个难度按钮与音乐开关正常渲染。
- 进入游戏：点「普通模式」→ `Displayed ... GameActivity: +292ms`，HUD 显示 `SCORE: 10 / LIFE: 100`，英雄机、多类敌机、背景滚动、自动射击均正常，Logcat 无 `FATAL EXCEPTION`。
- 触屏拖动：`input swipe 540 1750 200 1500 500` 后英雄机移到左下；本局自然结束时 `SCORE: 80 / LIFE: 0` 并弹出「游戏结束 / 本局得分：80 / 输入姓名」。
- 成绩与排行榜：输入 `leadtest` 保存后落地 `leadtest,80,2026-11-25 09:42:09,normal`；排行榜显示「normal 难度 得分排行榜 / 1. leadtest 80 分」。
- 重开局：返回主菜单再选难度，HUD 从 `SCORE: 0 / LIFE: 100` 重新开始。
- 线程自查：游戏内 `ps -AT | grep -c game-loop` = 1，按返回键后 = 0（绘制线程已停）。
- 音频：进游戏 3 s 与 8 s 分别查 `dumpsys media.audio_flinger` 均为 `1 active` 的 BGM 轨；关掉音乐开关后 `0 active`。

---

## 第 2 轮　修复：困难模式超级 Boss 血量倍率不生效

**问题现象**
困难模式下超级 Boss 血量与普通模式完全相同（均为 300），三档难度只有敌机生成节奏和射击节奏不同，"困难"缺少设计中的血量加成。

**原因**
桌面版 `edu.hitsz.application.HardGame.action()` 的每局开始时调用 `applyBossHpMultiplier()`：`bossHpMul += 0.2; superBossFactory = new SuperBossFactory(bossHpMul);`。
迁移时按 PPT「照搬纯逻辑」把 `applyBossHpMultiplier()` 方法原样搬进了 Android 的 `business/HardGame.java`，但没有搬调用点（桌面版在 `action()` 里，Android 的循环已上移到 `BaseGame.run()`）→ 方法成为死代码，倍率永远停留在初始值 `1.0`。

**改动内容**
- `android/app/src/main/java/edu/hitsz/aircraftwar/business/HardGame.java`：在构造器末尾（设置 `enemySpawnCycle = 12; shootCycle = 15;` 之后）调用一次 `applyBossHpMultiplier();`，并补 `Log.i(TAG, "困难模式开局，超级Boss血量倍率=" + bossHpMul);`（`TAG = "HardGame"`），使倍率可在 Logcat 直接核对。
- `android/app/src/main/java/edu/hitsz/aircraftwar/business/BaseGame.java`：`enemySpawnProcess()` 中生成超级 Boss 时先取出实例再入列，并 `Log.i(TAG, "生成超级 Boss，血量=" + superBoss.getMaxHp());`（异常分支 `Log.e(TAG, "生成超级 Boss 失败", e);`），用于实测取证。

**改动原因**
- 最小改动：不改变类结构、不引入新状态，只补回漏掉的调用点，与桌面版语义一致（每开一局困难模式倍率累加 0.2）。
- 加日志是为了让"倍率确实生效"这件事有可复核的运行时证据，而不是只靠读代码推断。

**实测结果**
- 构建：`assembleDebug` → `BUILD SUCCESSFUL`。
- 困难模式进游戏后 Logcat：`HardGame: 困难模式开局，超级Boss血量倍率=1.2`。
- 普通模式打到 200 分以上时 Logcat：`BaseGame: 生成超级 Boss，血量=300`（无倍率），画面出现巨型 Boss 且可被击落。
- 困难模式超级 Boss 血量应为 300 × 1.2 = 360：倍率 `1.2`（Logcat 实测）与基础血量 `300`（普通模式 Logcat 实测）都有运行时证据，但**困难模式下未直接观测到超级 Boss 血条**（困难模式节奏快，自动化脚本打到 110 分即结束），360 这一数值由两个实测值按 `SuperBossFactory((int)(300*hpMultiplier))` 推算得出，不作为独立实测结论。
