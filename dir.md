# 巧乐兹（qiaolezi）项目构成分析

> 本文档由对仓库 `D:\qiaolezi` 的完整静态分析生成，覆盖构建配置、源码构成、资源结构、注册链路与已核实的问题。
> 分析基准：HEAD = `52cc003`（2026-09-05）。分析后工作区又完成了 fabric-api 版本统一（`0.102.0+1.21.1`）与雪碧物品 ID 重命名（`spirite`）。

---

## 1. 项目概览

| 项目 | 内容 |
| --- | --- |
| 模组 ID | `qiaolezi` |
| 显示名称 | 巧乐兹 |
| 类型 | Minecraft **Fabric** 模组（非 Java 后端/Web 项目） |
| 作者 | `LYX_114514` |
| 当前版本 | `0.1.3`（`gradle.properties` → `fabric.mod.json` 三处一致） |
| 许可证 | CC0-1.0（仓库内 `LICENSE` 为 CC0 全文） |
| 主页 / 源码 | https://github.com/lyx2009 · https://github.com/lyx2009/qiaolezi |
| 项目描述 | 在 Minecraft 中加入「巧乐兹」与「雪碧」两种食物，以及「张雪峰」实体 |
| 代码规模 | 7 个 Java 文件（其中 2 个为空壳/占位），约 320 行；10 个资源文件 |

---

## 2. 技术栈与依赖

| 层次 | 组件 | 版本 | 来源 |
| --- | --- | --- | --- |
| 游戏 | Minecraft | `1.21.1` | `build.gradle` 硬编码 |
| 映射 | Yarn | `1.21.1+build.3` | `build.gradle` 硬编码 |
| 加载器 | Fabric Loader | `0.19.3` | `build.gradle` 硬编码 |
| API | Fabric API | `0.102.0+1.21.1` | `gradle.properties` → `build.gradle` 引用 |
| 构建插件 | fabric-loom | `1.17-SNAPSHOT` | `gradle.properties` → `${loom_version}` |
| 字节码目标 | Java | `21`（`release = 21`，source/target 21） | `build.gradle` |
| Gradle | Wrapper 发行版 | `9.5.1-bin` | `gradle/wrapper/gradle-wrapper.properties` |
| CI JDK | Microsoft OpenJDK | `25`（构建目标仍为 21） | `.github/workflows/build.yml` |
| 运行时依赖（`fabric.mod.json`） | fabricloader `>=0.19.3`、minecraft `~1.21.1`、java `>=21`、fabric-api `*` | | |

> ℹ️ `fabric_api_version` 已统一为实际使用的 `0.102.0+1.21.1` 并被 `build.gradle` 引用（✅ 已修复）；但 `minecraft_version`、`loader_version` 仍写死在 `build.gradle`，`yarn_mappings` 仍为注释未使用。详见第 8 节问题 4。

---

## 3. 目录结构

```
D:\qiaolezi\
├── .github/workflows/build.yml          # CI：push/PR 自动构建并上传 build/libs
├── .vscode/
│   ├── launch.json                      # 「Minecraft Client」「Minecraft Server」调试配置（devlaunchinjector）
│   └── settings.json                    # java.configuration.updateBuildConfiguration = interactive
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties         # Gradle 9.5.1
├── src/
│   ├── main/                            # ★ 双端共用（服务端 + 客户端）源码集
│   │   ├── java/com/qiaolezi/
│   │   │   ├── Qiaolezi.java             # 模组主入口 ModInitializer：定义/注册 2 个物品
│   │   │   ├── ModEntities.java          # 实体类型注册 + 默认属性绑定
│   │   │   ├── ModSpawn.java             # 下界自然生成规则
│   │   │   ├── ZhangxuefengEntity.java   # 「张雪峰」实体（继承原版僵尸）
│   │   │   ├── QiaoleziClient.java       # ⚠️ 空类（5 行、无内容、未注册）——模板残留
│   │   │   └── mixin/ExampleMixin.java   # ⚠️ 空注入（方法体只有注释）——模板残留
│   │   └── resources/
│   │       ├── fabric.mod.json           # 模组元数据 + 入口点声明
│   │       ├── qiaolezi.mixins.json      # ⚠️ 未被 fabric.mod.json 引用，不生效
│   │       ├── assets/qiaolezi/
│   │       │   ├── icon.png              # 模组图标
│   │       │   ├── lang/
│   │       │   │   ├── en_us.json        # 仅 2 条物品名
│   │       │   │   └── zh_cn.json        # 仅 2 条物品名
│   │       │   ├── models/item/
│   │       │   │   ├── qiaolezi.json     # parent: item/generated
│   │       │   │   └── spirite.json       # parent: item/generated
│   │       │   └── textures/
│   │       │       ├── entity/
│   │       │       │   └── ZhangxuefengEntity.png.resc   # 占位符（空文件，贴图待绘制）
│   │       │       └── item/
│   │       │           ├── qiaolezi.png        # 当前巧乐兹贴图
│   │       │           ├── qiaolezi_old.png    # 旧版贴图（无任何引用）
│   │       │           ├── qiaolezi_paint.png  # 绘制草稿（无任何引用）
│   │       │           └── spirite.png          # 雪碧贴图
│   │       └── data/qiaolezi/recipe/
│   │           ├── qiaolezi.json         # 巧乐兹合成配方
│   │           └── spirite.json           # 雪碧合成配方
│   └── client/                          # ★ 仅客户端源码集（loom splitEnvironmentSourceSets）
│       ├── java/com/qiaolezi/client/
│       │   ├── QiaoleziClient.java       # ⚠️ ClientModInitializer 空壳，未在入口点注册
│       │   └── ModEntityRenderers.java   # ★ 实际生效的客户端入口点：注册实体渲染器
│       └── resources/qiaolezi.client.mixins.json   # ⚠️ 未被引用，且 mixins 列表为空
├── build.gradle                          # loom 配置、依赖、Java 21、sourcesJar、maven-publish
├── settings.gradle                       # 插件仓库 maven.fabricmc.net；rootProject.name = qiaolezi
├── gradle.properties                     # loom/loader/mod 版本、JVM 参数
├── gradlew / gradlew.bat
├── LICENSE                               # CC0-1.0
├── README.md                             # 中文说明 + 版本更新记录 + 更新计划
├── .gitignore                            # 忽略 .gradle/ build/ bin/ run/ out/ .vscode/ hs_err_*.log 等
└── .gitattributes                        # gradlew=lf，*.bat=crlf

（以下为本地生成物，均被 .gitignore 忽略，不入库）
├── build/          # 产物：libs/qiaolezi-0.1.2.jar、libs/qiaolezi-0.1.2-sources.jar、classes、devlibs…
├── bin/            # Eclipse/IDE 编译输出（含 .class 与拷贝的资源）
├── run/            # 开发环境运行目录（logs、crash-reports、saves、mods、options.txt…）
├── .gradle/        # Gradle 缓存（含 loom-cache/launch.cfg，供 VS Code 调试使用）
├── hs_err_pid27604.log / replay_pid27604.log   # JVM 崩溃与飞行记录
```

---

## 4. 构建配置详解

### 4.1 `settings.gradle`
- `pluginManagement` 仓库：`https://maven.fabricmc.net/`、`mavenCentral()`、`gradlePluginPortal()`。
- `rootProject.name = 'qiaolezi'`（与模组 ID 一致）。

### 4.2 `gradle.properties`
```properties
org.gradle.jvmargs=-Xmx1G          # 仅 1G 堆（见问题 7，本地曾多次因内存不足崩溃）
org.gradle.parallel=true
org.gradle.configuration-cache=false   # 因 IDEA 与 loom 兼容性问题关闭
minecraft_version=1.21.1
# yarn_mappings=1.21.1+build.10       # 已注释，实际未使用
loader_version=0.19.3
loom_version=1.17-SNAPSHOT             # 被 build.gradle 引用（fabric_api_version 也已引用）
mod_version=0.1.2
maven_group=com.qiaolezi
fabric_api_version=0.102.0+1.21.1     # 被 build.gradle 以 ${project.fabric_api_version} 引用
```

### 4.3 `build.gradle` 关键点
| 配置项 | 内容 |
| --- | --- |
| 插件 | `fabric-loom`（版本取自 `${loom_version}`）、`maven-publish` |
| 坐标 | `version = mod_version`(0.1.2)、`group = maven_group`(`com.qiaolezi`) |
| 仓库 | `repositories {}` 为空——完全依赖 Loom 自动注入的 Minecraft/Maven 仓库 |
| `loom.splitEnvironmentSourceSets()` | **启用 main/client 双源码集分离**；`mods { "qiaolezi" { sourceSet main; sourceSet client } }` 将两者都纳入模组产物 |
| `processResources` | 对 `fabric.mod.json` 执行 `expand "version"` 占位符替换 |
| `JavaCompile` | `options.release = 21` |
| `java` | `withSourcesJar()`；source/target 兼容性 21 |
| `jar` | 将根目录 `LICENSE` 以 `LICENSE_qiaolezi` 之名打入 jar（已在产物中确认） |
| `publishing` | 声明 `mavenJava` 发布；发布仓库为空（未实际发布） |

> 未配置 `test` 源码集、无任何单元测试，也无 `fabric.mod.json` 的 mixin 声明。

### 4.4 CI（`.github/workflows/build.yml`）
- 触发：`push` 与 `pull_request`。
- 环境：`ubuntu-24.04` + JDK 25（Microsoft 发行版）；校验 wrapper → `./gradlew build` → 上传 `build/libs/` 为 Artifacts。

---

## 5. 源码构成（Java）

### 5.1 `main` 源码集（双端共用）

#### `Qiaolezi.java` — 模组主入口（`ModInitializer`，126 行，最核心）
- 常量：`MOD_ID = "qiaolezi"`、`LOGGER`（`LoggerFactory.getLogger(MOD_ID)`）。
- 以**匿名内部类方式**定义两个 `Item`（`QIAOLEZI`、`Spirite`），物品逻辑几乎全部集中在此文件。
- `onInitialize()` 中完成注册：
  1. `Registries.ITEM` 注册 `qiaolezi:qiaolezi`、`qiaolezi:spirite`；
  2. `ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)` 将其加入「食物与饮品」创造栏；
  3. 调用 `ModEntities.initialize()` 注册实体；
  4. 调用 `ModSpawn.initialize()` 注册下界生成。
- 工具方法 `id(String path)` → `Identifier.of(MOD_ID, path)`。
- 代码中保留了已废弃的酿造台配方代码（注释掉的 `BrewingRecipeRegistry` / `ModPotions`）。
- 注释含「菩萨保佑不出bug」「这Fabric真他妈难写，下次用NeoForge」等口语化标记。

**物品数值一览**

| 属性 | 巧乐兹 `qiaolezi:qiaolezi` | 雪碧 `qiaolezi:spirite` |
| --- | --- | --- |
| 最大堆叠 | 64 | 16 |
| 饱食度 `nutrition` | 4 | 2 |
| 饱和度系数 | 0.3 | 0.1 |
| 速度 SPEED | 1000 tick，等级 9，**100%** | 500 tick，等级 9，**100%** |
| 力量 STRENGTH | 500 tick，等级 0，**100%** | — |
| 中毒 POISON | 100 tick，等级 2，20% | 100 tick，等级 2，20% |
| 反胃 NAUSEA | 500 tick，等级 2，20% | 500 tick，等级 2，20% |
| 瞬间伤害 INSTANT_DAMAGE | 100 tick，等级 2，10% | 100 tick，等级 2，5% |
| 特性 | `snack()` + `alwaysEdible()` | `snack()` + `alwaysEdible()`，重写 `getUseAction()=DRINK`、`getMaxUseTime()=32`（饮用动作） |
| 描述（LORE） | 「你跑不过我你信不信」金色 | 「美味雪碧,喝了能见张雪峰」金色 |
| 稀有度 | `EPIC` | `EPIC` |

#### `ModEntities.java` — 实体注册（29 行）
- 注册 `EntityType<ZhangxuefengEntity>`，ID `qiaolezi:zhangxuefeng`，`SpawnGroup.MONSTER`，碰撞箱 `0.6F × 1.95F`（与僵尸一致）。
- `initialize()` 通过 `FabricDefaultAttributeRegistry.register(...)` 绑定 `ZhangxuefengEntity.createZhangxuefengAttributes().build()`。

#### `ModSpawn.java` — 自然生成（31 行）
- `BiomeModifications.create(qiaolezi:zhangxuefeng_spawn)` + `ModificationPhase.ADDITIONS`。
- 选择器：`BiomeSelectors.foundInTheNether()` —— **仅下界**。
- 生成条目：`SpawnGroup.MONSTER`，权重 **80**，最小 1 只、最大 3 只成群。

#### `ZhangxuefengEntity.java` — 「张雪峰」实体（85 行）
- **继承原版 `ZombieEntity`**，因此自动具备僵尸的全部行为（追击、破门、白天燃烧、幼年变体、僵尸增援等）。
- 属性（在 `createZombieAttributes()` 基础上覆盖同名属性）：
  | 属性 | 数值 | 原版僵尸 |
  | --- | --- | --- |
  | `MAX_HEALTH` | 40.0 | 20.0 |
  | `MOVEMENT_SPEED` | 0.28 | 0.23 |
  | `ATTACK_DAMAGE` | 5.0 | 3.0 |
  | `FOLLOW_RANGE` | 48.0 | 35.0 |
- `initGoals()` 被重写但**仅调用 `super`**（未定制 AI，属冗余代码）。
- 重写 `dropLoot(DamageSource, boolean)`：**仅当被玩家击杀且位于 `ServerWorld`** 时追加掉落。
  **掉落表**：
  | 概率 | 掉落物 |
  | --- | --- |
  | 100% | 巧乐兹 ×1（必掉） |
  | 30% | 额外巧乐兹 ×1 |
  | 40% | 雪碧 ×1 |
  | 25% | 「张雪峰的跑鞋」= 铁靴 + **灵魂疾行 III**（通过 `ItemEnchantmentsComponent.Builder` 在 `DataComponentTypes.ENCHANTMENTS` 上设置），并用 `DataComponentTypes.CUSTOM_NAME` 命名为「张雪峰的跑鞋」 |

#### 模板残留（无实际作用）
- `QiaoleziClient.java`（main 源码集）：只有 `public class QiaoleziClient {}`，无接口实现、无注册，却被编译进 jar。
- `mixin/ExampleMixin.java`：`@Mixin(MinecraftServer.class)` 注入 `loadLevel()` 的 `HEAD`，**方法体只有注释**，不产生任何效果。

### 5.2 `client` 源码集（仅客户端）

| 文件 | 说明 | 是否生效 |
| --- | --- | --- |
| `ModEntityRenderers.java` | 实现 `ClientModInitializer`，通过 `EntityRendererRegistry.register` 为 `ZHANGXUEFENG` 注册一个继承 `ZombieEntityRenderer` 的匿名渲染器（复用 `EntityModelLayers.ZOMBIE/INNER_ARMOR/OUTER_ARMOR` 模型层），并重写 `getTexture()` 返回 `qiaolezi:textures/entity/zhangxuefeng.png` | ✅ `fabric.mod.json` 中已注册为 client 入口点 |
| `QiaoleziClient.java` | 实现 `ClientModInitializer`，`onInitializeClient()` 为空 | ❌ 未在任何入口点声明 |

---

## 6. 资源构成

### 6.1 `fabric.mod.json`（模组元数据）
| 字段 | 值 |
| --- | --- |
| `schemaVersion` / `id` / `version` | 1 / `qiaolezi` / `0.1.2` |
| `name` / `description` | 巧乐兹 / 在我的世界中加入巧乐兹 |
| `authors` | `LYX_114514` |
| `license` / `icon` | `CC0-1.0` / `assets/qiaolezi/icon.png` |
| `environment` | `*`（双端） |
| `entrypoints.main` | `com.qiaolezi.Qiaolezi` |
| `entrypoints.client` | `com.qiaolezi.client.ModEntityRenderers` |
| `depends` | fabricloader `>=0.19.3`、minecraft `~1.21.1`、java `>=21`、fabric-api `*` |
| `mixins` | **缺失**（见问题 2） |

### 6.2 语言文件
两个文件均只包含 **2 条物品名**，无实体名、无物品组名、无提示文本：
```json
// zh_cn.json                          // en_us.json
"item.qiaolezi.qiaolezi": "巧乐兹"      "item.qiaolezi.qiaolezi": "QiaoLeZi"
"item.qiaolezi.spirite":   "雪碧"        "item.qiaolezi.spirite":   "Sprite"
```

### 6.3 模型
两个物品模型均为标准 `item/generated`，各引用 `qiaolezi:item/qiaolezi` 与 `qiaolezi:item/spirite` 贴图，无自定义模型/方块状态。

### 6.4 配方（`data/qiaolezi/recipe/`）
**巧乐兹**（有序合成，产出 3 个）
```
M L M       M = 奶桶 (milk_bucket)
C P C       L = 可可豆 (cocoa_beans)
S S S       C = 糖 (sugar)      P = 紫色染料 (purple_dye)      S = 雪块 (snow_block)
```
**雪碧**（有序合成，产出 3 个）
```
G G G       G = 绿宝石 (emerald)
C C C       C = 糖 (sugar)      W = 药水 (potion)
W W W
```

### 6.5 贴图
| 文件 | 状态 |
| --- | --- |
| `item/qiaolezi.png` | ✅ 被模型引用 |
| `item/spirite.png` | ✅ 被模型引用 |
| `item/qiaolezi_old.png` | ⚪ 孤立资源（全仓库无引用），仍被打进 jar |
| `item/qiaolezi_paint.png` | ⚪ 孤立资源（全仓库无引用），仍被打进 jar |
| `entity/ZhangxuefengEntity.png.resc` | ⚪ 占位符（空文件，实体贴图待绘制，可忽略） |

---

## 7. 注册链路与运行产物

### 7.1 初始化流程
```
Fabric Loader 启动
   ├─ main 入口点  →  com.qiaolezi.Qiaolezi#onInitialize()
   │      ├─ Registries.ITEM  注册 qiaolezi:qiaolezi / qiaolezi:spirite
   │      ├─ ItemGroupEvents  加入 FOOD_AND_DRINK 创造栏
   │      ├─ ModEntities.initialize()  → EntityType 注册 + 默认属性绑定
   │      └─ ModSpawn.initialize()     → 下界 MONSTER 生成（权重 80, 1~3）
   └─ client 入口点 →  com.qiaolezi.client.ModEntityRenderers#onInitializeClient()
          └─ EntityRendererRegistry.register(ZHANGXUEFENG, 僵尸渲染器匿名子类)
                 └─ getTexture() → qiaolezi:textures/entity/zhangxuefeng.png  ← 占位符（空文件，贴图待绘制）
```
（`qiaolezi.mixins.json` / `qiaolezi.client.mixins.json` **不在上述链路中**，因为 `fabric.mod.json` 未声明 `mixins`。）

### 7.2 构建产物 `build/libs/qiaolezi-0.1.2.jar`（36,906 字节，构建于 2026-09-05 22:13）
```
fabric.mod.json
LICENSE_qiaolezi
qiaolezi.mixins.json              ← 被打包但不会加载
qiaolezi.client.mixins.json       ← 被打包但不会加载
com/qiaolezi/{Qiaolezi,Qiaolezi$1,QiaoleziClient,ModEntities,ModSpawn,ZhangxuefengEntity}.class
com/qiaolezi/client/{ModEntityRenderers,ModEntityRenderers$1,QiaoleziClient}.class
com/qiaolezi/mixin/ExampleMixin.class
assets/qiaolezi/{icon.png, lang/*, models/item/*, textures/item/*, textures/entity/ZhangxuefengEntity.png.resc}
data/qiaolezi/recipe/{qiaolezi.json, spirite.json}
```
> 结论：**构建可通过**，双源码集产物正确合并进同一个 jar，`com/qiaolezi/client/ModEntityRenderers.class` 确实存在（即客户端入口点可被加载）。

### 7.3 开发/运行环境
- `run/` 为 loom 的开发运行目录，已装入 `modmenu-11.0.4`、`placeholder-api-2.4.2+1.21`、`jei-1.21.1-fabric-19.27.0.343` 三个模组。
- `run/crash-reports/` 有两份 2026-09-05 的客户端崩溃（21:50、21:56），以及三份 2026-06-27 的崩溃。
  最近的崩溃为：
  ```
  java.lang.NullPointerException: Cannot invoke "...EntityRenderer.shouldRender(...)" because "entityRenderer" is null
      at EntityRenderDispatcher.shouldRender(EntityRenderDispatcher.java:134)
  ```
  即**世界中存在未注册客户端渲染器的实体**。两份崩溃的时间点均**早于**实体/渲染器代码的提交（`854a993`，22:23）与最后一次成功构建（22:13），说明该问题在加入 `ModEntityRenderers` 之前出现，属于历史记录；当前代码已为 `zhangxuefeng` 注册渲染器。
- 根目录与 `run/` 下的 `hs_err_pid*.log` 显示 JVM 因 **原生内存分配失败**（`malloc failed ... Chunk::new`）退出。`gradle.properties` 中 `org.gradle.jvmargs=-Xmx1G` 与开发运行时并发占用内存是本机崩溃的常见诱因。

---

## 8. 已核实的问题与风险

| # | 严重度 | 问题 | 证据 | 建议 |
| --- | --- | --- | --- | --- |
| 1 | ⚪ 占位符 | **实体贴图为占位符（可忽略）**：`textures/entity/ZhangxuefengEntity.png.resc` 为空文件，仅作占位，贴图待后续绘制（README「更新计划：绘制贴图」）。 | `src/main/resources/assets/qiaolezi/textures/entity/` | 绘制贴图后替换空文件并统一文件名 |
| 2 | 🔴 高 | **`qiaolezi.mixins.json` 未被加载**：`fabric.mod.json` 没有 `"mixins"` 字段，两个 mixin 配置均不生效（`ExampleMixin` 与客户端 mixin 列表）。 | `fabric.mod.json` 全文（仅 `entrypoints`/`depends`）；jar 内含两个 mixins.json | 在 `fabric.mod.json` 增加 `"mixins": ["qiaolezi.mixins.json", {"config": "qiaolezi.client.mixins.json", "environment": "client"}]`；若不需要，直接删除配置与 `ExampleMixin` |
| 3 | 🟡 中 | **实体缺少语言键**：`ModEntities` 注册 ID 为 `zhangxuefeng`，翻译键应为 `entity.qiaolezi.zhangxuefeng`，但两个语言文件都没有该键，游戏内会直接显示原始键名。掉落物「张雪峰的跑鞋」也是硬编码 `Text.literal`，无法本地化。 | `lang/*.json` 均仅 2 条；`ZhangxuefengEntity.java:79` | 补充 `"entity.qiaolezi.zhangxuefeng": "张雪峰"`（以及 en_us），并考虑改用 `Text.translatable` |
| 4 | 🟡 中（✅ fabric-api 已修复） | **依赖版本配置不一致**：`fabric_api_version` 已统一为实际使用的 **0.102.0+1.21.1** 并重新接入 `build.gradle`；但 `minecraft_version`、`loader_version` 仍写死在 `build.gradle`，`yarn_mappings` 仍为注释未使用。 | `gradle.properties:9-20` 对比 `build.gradle:28-34` | 可继续将 `minecraft`、`loader` 改用 `"${project.xxx}"` 引用 |
| 5 | 🟡 中 | **存在两个空壳客户端类，职责混乱**：`main` 下的 `com.qiaolezi.QiaoleziClient`（空类，未注册、无意义）与 `client` 下的 `com.qiaolezi.client.QiaoleziClient`（空初始化器，未注册）并存，真正的客户端入口是 `ModEntityRenderers`。 | `src/main/java/com/qiaolezi/QiaoleziClient.java`（5 行）、`src/client/java/com/qiaolezi/client/QiaoleziClient.java`（10 行）、`fabric.mod.json` client 入口点 | 删除 `main` 下的空类；删除或合并 `client` 下的空初始化器 |
| 6 | 🟢 低 | **孤立资源**：`qiaolezi_old.png`、`qiaolezi_paint.png` 无任何引用，但会随 jar 发布，增大体积。 | 全 `src` 目录搜索无匹配；已在 jar 目录清单中确认 | 移出 `src` 或删除 |
| 7 | 🟢 低 | **冗余/遗留代码**：`ZhangxuefengEntity.initGoals()` 仅调用 `super`；`ExampleMixin` 注入体为空；`Qiaolezi.onInitialize()` 保留注释掉的酿造台配方；`Qiaolezi.QIAOLEZI` 方法体后有 3 行无关注释。 | 对应源码各行 | 清理 |
| 8 | ✅ 已修复 | **命名拼写**：雪碧物品已由旧 ID 全量重命名为 `spirite`（`Spirite`/`SPIRITE`），消除与「雪碧 Sprite」的语义混淆。 | 工作区已改（Java 变量、注册 ID、配方/模型/贴图/语言键） | 常量可进一步统一为大写下划线 `SPIRITE`（可选） |
| 9 | 🟢 低 | **无测试、无自动化检查**：无 `src/test`，CI 仅执行 `build`，不校验资源完整性与语言键。 | 目录结构、`build.yml` | 可加入资源/语言键校验任务 |
| 10 | ⚪ 提示 | **CI JDK 25 与目标 Java 21 不一致**：`--release 21` 可正常产出 Java 21 字节码，但本地与 CI 环境版本不统一。 | `.github/workflows/build.yml:20`、`build.gradle:46` | 统一为 21，或明确说明使用 25 编译的目标字节码版本 |
| 11 | ✅ 已修复 | **刷怪蛋**：已为 `张雪峰` 注册 `zhangxuefeng_spawn_egg`（复用僵尸刷怪蛋配色 `0x00AFAF`/`0x799C65`，加入「刷怪蛋」创造栏）。 | `ModEntities.java` | — |

---

## 9. 版本演进（git 历史）

| 提交 | 日期 | 内容 |
| --- | --- | --- |
| `7a0ca5a` | 2026-07-07 | 初始提交：Fabric 模板工程（gradlew、CI、LICENSE、README、示例 mixin、lang、icon、巧乐兹贴图与配方） |
| `4d7b77e` | 2026-07-07 | 修改 `fabric.mod.json` |
| `339c751` | 2026-07-07 | 修改 `README.md` |
| `7949207` | 2026-07-08 | 修改 `Qiaolezi.java`，新增 `main` 下的空 `QiaoleziClient.java` |
| `b8524ff` | 2026-07-08 | **0.1.1**：新增雪碧模型与贴图（`spirite.png`）、保留 `qiaolezi_old.png`/`qiaolezi_paint.png`、更新巧乐兹贴图 |
| `47933a8` | 2026-07-08 | 调整 `gradle.properties` 与 `fabric.mod.json` |
| `854a993` | 2026-09-05 | **0.1.2 主体**：新增「张雪峰」实体全套（`ModEntities`/`ModSpawn`/`ZhangxuefengEntity`/`ModEntityRenderers`）、实体贴图占位文件、`build.gradle` 与 `gradle.properties` 调整、`Qiaolezi.java` 增加实体与生成注册 |
| `a2dc7db` | 2026-09-05 | 修改 `fabric.mod.json`（补 client 入口点） |
| `52cc003` | 2026-09-05 | 更新 README（记录 0.1.2） |

README 记录的版本演进与代码一致：`0.1.0` 加入巧乐兹与雪碧 → `0.1.1` 重绘/新增贴图并改配方 → `0.1.2` 加入张雪峰实体（贴图未完成）。

---

## 10. 开发与构建命令

| 目的 | 命令 |
| --- | --- |
| 构建（生成 `build/libs/qiaolezi-0.1.2.jar`） | `.\gradlew build` |
| 清理 | `.\gradlew clean` |
| 启动带模组的客户端（开发环境） | `.\gradlew runClient`（或在 VS Code 用 `launch.json` 的 “Minecraft Client”） |
| 启动服务端 | `.\gradlew runServer`（或 “Minecraft Server”） |
| 仅重新映射/生成 IDEA 工程 | `.\gradlew genSources` / `.\gradlew idea` |

调试配置通过 `-Dfabric.dli.config=D:\qiaolezi\.gradle\loom-cache\launch.cfg` 指向 loom 生成的启动配置，工作目录为 `${workspaceFolder}/run`。

---

## 11. 后续计划（摘自 README）

1. 绘制贴图（`张雪峰` 实体贴图现为占位符，待补充）。
2. 增加击败音效。

> README 备注：「85% 的代码都是 D 老师写的」。

---

## 12. 总结

该项目是一个**结构清晰但尚未完工的小型 Fabric 模组**：采用 Loom 的 main/client 双源码集分离布局，正确定义并注册了 2 个食物物品（含药水效果、LORE、稀有度与饮用动作）、2 个合成配方、1 个自定义实体与下界生成规则，构建链路完整且产物正确合并。

代码组织的突出特点是**物品定义集中在 `Qiaolezi.java` 一个文件**（以匿名内部类承载特性），实体相关逻辑按「注册（`ModEntities`）/ 生成（`ModSpawn`）/ 行为（`ZhangxuefengEntity`）/ 渲染（`ModEntityRenderers`）」拆分，职责划分合理。

但存在一处会**直接影响游戏内表现**的缺陷：`fabric.mod.json` 未声明 mixins 导致两个 mixin 配置完全失效；另有空壳客户端类冗余、语言键缺失、`minecraft`/`loader` 版本属性仍未接入等问题。实体贴图目前为占位符（待绘制），fabric-api 版本已统一为 `0.102.0+1.21.1` 并完成雪碧物品 ID 重命名（`spirite`）。
