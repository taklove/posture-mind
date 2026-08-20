# 正形 PostureMind

> **找到该有力却无力的肌肉 —— 让每个人拥有自己的体态教练**

一个 AI 姿态识别 + 运动康复知识库驱动的体态矫正 APP。

## 🌟 核心理念

**体态问题，不是"哪里太紧"，而是"哪里该发力却没发力"。**

每一块紧张的肌肉背后，都有一块沉睡的肌肉。我们的产品定位不是"治症状"，而是"治根源"。

## 📦 项目结构

```
posture-mind/
├── index.html              # PWA 入口
├── styles.css              # PWA 样式
├── app.js                  # PWA 主逻辑
├── knowledge.js            # 8 种体态问题 + 14 训练动作（灵魂）
├── analyzer.js             # 关键点分析
├── exercises-ui.js         # 训练动作 SVG 插画
├── manifest.json           # PWA 配置
│
├── android/                # Android 原生版
│   ├── app/src/main/java/com/posturemind/app/
│   │   ├── data/Knowledge.kt       ← 与 JS 版逻辑一致
│   │   ├── data/PostureAnalyzer.kt
│   │   ├── camera/PoseDetector.kt
│   │   ├── ui/...                  ← Jetpack Compose UI
│   │   └── ...
│   └── README.md           # Android 构建说明
│
├── docs/
│   └── 商业计划书.md         # 商业计划
│
└── README.md
```

## 🚀 三个产品形态

| 形态 | 状态 | 链接/路径 |
|---|---|---|
| **PWA（在线演示）** | ✅ 已上线 | https://krfm2m3gl6wk2.space.mcode.cn |
| **Android 原生** | ✅ 代码完成，待 Android Studio 打开 | `android/` |
| **iOS 原生** | 📅 规划中 | 路线图见下 |

## 🎯 8 种检测的体态问题

1. **头前伸 + 圆肩** (FHP + Rounded Shoulders)
2. **骨盆前倾** (Anterior Pelvic Tilt)
3. **骨盆后倾** (Posterior Pelvic Tilt)
4. **膝内扣** (Knee Valgus)
5. **膝过伸** (Knee Hyperextension)
6. **高低肩** (Shoulder Asymmetry)
7. **翼状肩胛** (Winged Scapula)
8. **头部侧倾** (Lateral Head Tilt)

每种问题都标注：
- 🔴 **代偿肌**（看起来紧的，不是根源）
- 🟢 **该发力却没发力的**（这才是根源）

## 🛠 技术栈

### PWA 版
- 原生 HTML / CSS / JS（零依赖）
- MediaPipe Pose（CDN）
- LocalStorage

### Android 版
- Kotlin 2.0 + Jetpack Compose
- CameraX
- MediaPipe Tasks Vision
- DataStore Preferences
- Coroutines + Flow

## 📱 快速开始

### 测试 PWA
1. 手机浏览器打开 https://krfm2m3gl6wk2.space.mcode.cn
2. 浏览器菜单 → 添加到主屏幕
3. 像 APP 一样使用

### 运行 Android 版
详见 [`android/README.md`](./android/README.md)

## 🗓 路线图

| 阶段 | 时间 | 目标 |
|---|---|---|
| P0 验证 | M0-M1 | PWA 上线，500 用户测试 |
| P1 商业化 | M2-M3 | 订阅功能上线，1 万付费用户 |
| P2 双端 | M4-M6 | iOS + Android 上架 |
| P3 视频 + 智能 | M7-M12 | 视频评估 + AI 教练 |
| P4 B 端 | M12-M18 | 企业客户 + 教练端 |
| P5 规模 | M18-M24 | 数据飞轮 + 新品类 |

## 📄 文档

- [商业计划书](./docs/商业计划书.md)
- [Android 构建说明](./android/README.md)
- [PWA 模型说明](./android/app/src/main/assets/README.md)

## 👥 团队需求

我们在找：
- **CEO**：互联网产品 + 健身/医疗跨界
- **CTO**：AI/移动端背景，MediaPipe 经验
- **内容负责人（关键）**：运动康复师/物理治疗师，3 年+经验

## 📝 License

待定（默认保留所有权利）

---

**让每一块紧张的肌肉背后，都有一块被唤醒的肌肉。**
