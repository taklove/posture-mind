package com.posturemind.app.camera

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.posturemind.app.data.PostureAnalyzer

/**
 * MediaPipe Pose 检测器（包装器）
 *
 * 负责：
 * 1. 加载 MediaPipe 模型
 * 2. 接收 Bitmap，返回关键点
 * 3. 关闭资源
 */
class PoseDetector(
    private val context: Context,
    private val onResult: (PoseLandmarkerResult?) -> Unit
) {
    private var poseLandmarker: PoseLandmarker? = null
    private val analyzer = PostureAnalyzer()

    /**
     * 初始化 MediaPipe Pose 模型
     * modelPath 相对于 assets/
     */
    fun setup(modelPath: String = "pose_landmarker_lite.task") {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelPath)
                .build()

            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.IMAGE)
                .setNumPoses(1)
                .setMinPoseDetectionConfidence(0.5f)
                .setMinPosePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
            Log.d(TAG, "✅ MediaPipe Pose initialized")
        } catch (e: Exception) {
            Log.e(TAG, "❌ MediaPipe setup failed", e)
        }
    }

    /**
     * 检测 Bitmap
     * 旋转角度用于处理相机方向
     */
    fun detect(bitmap: Bitmap, imageRotation: Int = 0) {
        val landmarker = poseLandmarker ?: return

        val mpImage = BitmapImageBuilder(bitmap).build()
        val result = try {
            landmarker.detect(mpImage)
        } catch (e: Exception) {
            Log.e(TAG, "Detection failed", e)
            null
        }
        onResult(result)
    }

    /**
     * 转换 MediaPipe 关键点到 PostureAnalyzer.Point
     */
    fun toAnalyzerPoints(result: PoseLandmarkerResult?): List<PostureAnalyzer.Point> {
        if (result == null || result.landmarks().isEmpty()) return emptyList()
        val lm = result.landmarks()[0]
        return lm.map {
            PostureAnalyzer.Point(
                x = it.x(),
                y = it.y(),
                z = it.z(),
                visibility = it.visibility() ?: 0f
            )
        }
    }

    fun close() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    companion object {
        private const val TAG = "PoseDetector"
    }
}
