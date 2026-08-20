# GitHub Actions 自动化构建

## 🚀 快速开始

### 1. 把代码推到 GitHub

```bash
cd posture-mind
git init
git add .
git commit -m "init: posture mind pwa + android"
# 创建 GitHub 仓库后：
git remote add origin https://github.com/YOUR_USERNAME/posture-mind.git
git push -u origin main
```

### 2. 第一次构建

推代码后，GitHub Actions 会**自动开始构建**。去仓库的 **Actions** 标签页看进度。

构建完成后（**约 8-15 分钟**），在 **Artifacts** 区域下载 APK：
- `posturemind-apk`：包含所有构建产物

### 3. 安装到手机

下载后：
- **Android**：直接点击 APK 安装（要开"未知来源"）
- **电脑**：用 ADB `adb install app-debug.apk`

---

## 📦 构建产物

构建类型由触发方式决定：

| 触发方式 | 构建类型 | 用途 |
|---|---|---|
| Push 到 main | debug | 测试、CI |
| PR | debug | 验证 |
| 手动 (workflow_dispatch) | debug 或 release | 你选 |
| Git tag (v*) | release | 发版 |

---

## 🔐 配置发布签名（可选）

默认构建是 debug 签名（用 Android 默认调试 keystore），可以安装但**不能上架**。

要生成 release APK（可上架 Google Play），需要：

### 步骤 A：生成你自己的 keystore

在本地（Mac/Linux）跑：
```bash
keytool -genkey -v \
  -keystore posturemind-release.jks \
  -alias posturemind \
  -keyalg RSA -keysize 2048 \
  -validity 10000
```

会要你输密码，记下来。

### 步骤 B：base64 编码 keystore

```bash
# Mac/Linux
base64 -i posturemind-release.jks | tr -d '\n' > keystore.b64
# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("posturemind-release.jks")) | Out-File -Encoding ASCII keystore.b64
```

### 步骤 C：添加到 GitHub Secrets

去 GitHub 仓库 → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**：

| Secret 名 | 值 |
|---|---|
| `KEYSTORE_BASE64` | 上面 `keystore.b64` 的内容 |
| `KEYSTORE_PASSWORD` | 步骤 A 的 keystore 密码 |
| `KEY_ALIAS` | `posturemind` |
| `KEY_PASSWORD` | 步骤 A 的 key 密码（通常和 keystore 一样） |

### 步骤 D：触发 release 构建

**方法 1：手动触发**
1. GitHub → **Actions** → **Build Android APK** → **Run workflow**
2. Build type 选 `release`
3. 等构建完，下载 `posturemind-apk` artifact

**方法 2：打 tag 自动触发**
```bash
git tag v1.0.0
git push --tags
# GitHub Actions 自动构建 release APK + 创建 GitHub Release
```

---

## 📤 自动发布到 Google Play（可选）

需要再加一个 secret：`PLAY_STORE_SERVICE_ACCOUNT`
（Google Cloud Console → 创建 Service Account → 启用 Play Store API → 下载 JSON → 把整个 JSON 内容作为 secret）

配置好后，每次打 tag 都会自动上传到 Google Play **internal track**（先小范围测试用）。

---

## 🔧 故障排查

### 构建失败：找不到 MediaPipe 模型
检查 `android/app/src/main/assets/pose_landmarker_lite.task` 是否存在。Workflow 会自动下载，但本地构建需要手动：
```bash
mkdir -p android/app/src/main/assets
curl -L -o android/app/src/main/assets/pose_landmarker_lite.task \
  "https://storage.googleapis.com/mediapipe-models/pose_landmarker/pose_landmarker_lite/float16/1/pose_landmarker_lite.task"
```

### Gradle 同步失败
- 检查 `gradle/wrapper/gradle-wrapper.properties` 版本
- 检查 `gradle/libs.versions.toml` 里的版本号

### OOM（Out of Memory）
GitHub Actions 免费版有 7GB 内存限制。MediaPipe 模型较大，可能 OOM。解决：
- 用 `gradle.properties` 加 `org.gradle.jvmargs=-Xmx4g`
- 或用 GitHub Actions 大内存 runner（付费）

---

## 📊 当前状态

- ✅ Push to main → 自动 debug APK
- ✅ PR → 自动 debug APK 验证
- ✅ 手动 → 可选 debug/release
- ✅ 打 tag → 自动 release + GitHub Release
- 🔧 签名：需要配置 KEYSTORE_* secrets
- 🔧 Play Store：需要配置 PLAY_STORE_SERVICE_ACCOUNT
