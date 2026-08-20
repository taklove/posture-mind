# MediaPipe Pose 模型

需要把 `pose_landmarker_lite.task` 模型文件放到这个目录。

下载链接（任选一个）：

1. **Google 官方（最新）**：
   ```
   https://storage.googleapis.com/mediapipe-models/pose_landmarker/pose_landmarker_lite/float16/1/pose_landmarker_lite.task
   ```

2. **jsDelivr 镜像**（可能不是最新）：
   ```
   https://cdn.jsdelivr.net/npm/@mediapipe/tasks-vision@0.10.14/wasm/pose_landmarker_lite.task
   ```

3. **直接命令下载**（PowerShell）：
   ```powershell
   Invoke-WebRequest -Uri "https://storage.googleapis.com/mediapipe-models/pose_landmarker/pose_landmarker_lite/float16/1/pose_landmarker_lite.task" -OutFile "pose_landmarker_lite.task"
   ```

模型大小约 5MB，放在 `app/src/main/assets/` 下，编译时会自动打包到 APK。
