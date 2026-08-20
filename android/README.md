# 正形 PostureMind · Android 原生 APP

> 找到该有力却无力的肌肉 —— 让每个人拥有自己的体态教练

## 项目结构

```
android/
├── settings.gradle.kts          # 项目根配置
├── build.gradle.kts             # 顶层 build 配置
├── gradle.properties            # Gradle 全局属性
├── gradle/
│   ├── libs.versions.toml       # 依赖版本管理
│   └── wrapper/
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts         # app 模块配置
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── assets/
        │   └── pose_landmarker_lite.task  ← 需要手动下载
        ├── res/                 # 资源文件
        └── java/com/posturemind/app/
            ├── MainActivity.kt
            ├── PostureMindApplication.kt
            ├── data/            # 数据层
            │   ├── Model.kt          # 数据类
            │   ├── Knowledge.kt      # 8 种体态问题 + 14 训练动作 ← 灵魂
            │   ├── PostureAnalyzer.kt # 关键点分析
            │   └── HistoryStore.kt   # 本地存储
            ├── camera/
            │   ├── PoseDetector.kt   # MediaPipe 包装
            │   └── PoseOverlay.kt    # 骨骼叠加层
            ├── viewmodel/
            │   └── PostureViewModel.kt
            └── ui/
                ├── theme/            # 主题/颜色/字体
                ├── nav/              # 导航
                ├── home/             # 首页
                ├── capture/          # 拍照评估
                ├── result/           # 诊断结果
                ├── training/         # 训练列表
                ├── exercise/         # 训练详情
                ├── progress/         # 进度记录
                └── about/            # 关于/理念
```

## 技术栈

| 模块 | 选型 | 版本 |
|---|---|---|
| 语言 | Kotlin | 2.0.21 |
| 构建 | Android Gradle Plugin | 8.7.3 |
| UI | Jetpack Compose (BOM) | 2024.10.01 |
| 相机 | CameraX | 1.3.4 |
| 姿态检测 | MediaPipe Tasks Vision | 0.10.14 |
| 本地存储 | DataStore Preferences | 1.1.1 |
| 序列化 | kotlinx-serialization | 1.7.3 |
| 最低 SDK | Android 7.0 (Nougat) | API 24 |
| 目标 SDK | Android 15 | API 35 |

## 怎么在 Android Studio 里跑

### 前置条件
- Android Studio Hedgehog (2023.1.1) 或更新
- JDK 17
- Android SDK 35 + Build Tools 35.0.0
- 真机或模拟器（Android 7.0+）

### 步骤

1. **打开项目**
   - 启动 Android Studio
   - `File → Open` → 选择 `android/` 文件夹
   - 等待 Gradle 同步（首次较慢，需下载依赖）

2. **下载 MediaPipe 模型**
   - 在 `app/src/main/assets/` 目录下执行：
     ```powershell
     Invoke-WebRequest -Uri "https://storage.googleapis.com/mediapipe-models/pose_landmarker/pose_landmarker_lite/float16/1/pose_landmarker_lite.task" -OutFile "pose_landmarker_lite.task"
     ```
   - 文件大小约 5MB

3. **运行**
   - 连接 Android 设备（开发者模式 + USB 调试）
   - 点击工具栏 ▶️ Run
   - 选择设备

### 常见问题

**Q: Gradle 同步失败？**
A: 检查网络 + Gradle 版本。可手动改 `gradle-wrapper.properties` 中的 Gradle 版本。

**Q: `pose_landmarker_lite.task` 找不到？**
A: 必须放在 `app/src/main/assets/` 下，文件名要完全一致。

**Q: 相机黑屏？**
A: 检查 AndroidManifest.xml 权限 + 设备的相机权限设置。

**Q: 真机运行慢？**
A: MediaPipe 模型较重。可改用 `pose_landmarker_full.task`（更准但更慢）或 `pose_landmarker_heavy.task`（最准最慢）。

## 与 PWA 版的关系

Android 原生版是 PWA 版的原生重写。

| 维度 | PWA | Android |
|---|---|---|
| 包大小 | 0（在线） | ~30MB（含模型） |
| 启动速度 | 依赖网络 | 极快（本地） |
| 相机调用 | Web API | CameraX（更稳） |
| 离线 | 部分 | 完全离线 |
| 性能 | 一般 | 原生，更流畅 |

**核心代码（Knowledge.kt, PostureAnalyzer.kt）在两个平台逻辑一致**。改一处，两边都生效。

## 未来开发

### 待办（按优先级）

- [ ] Hilt 依赖注入
- [ ] Room 数据库（替代 DataStore 存历史）
- [ ] 视频评估（替代照片，分析动态）
- [ ] AI 教练（GPT API）
- [ ] Apple Watch / Wear OS 联动
- [ ] 国际化（i18n）
- [ ] 单元测试 + UI 测试
- [ ] ProGuard 规则优化

### 上架清单

- [ ] Privacy Policy
- [ ] 应用截图（多设备）
- [ ] 应用描述（中/英）
- [ ] 内容分级问卷
- [ ] 签名（release keystore）
- [ ] 隐私合规（中国 / GDPR / CCPA）
- [ ] 备案（如果上国内市场）

---

参考 PWA 演示版：https://krfm2m3gl6wk2.space.mcode.cn
