package com.posturemind.app.ui.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.posturemind.app.camera.PoseDetector
import com.posturemind.app.camera.PoseOverlay
import com.posturemind.app.data.CaptureView
import com.posturemind.app.data.PostureAnalyzer
import com.posturemind.app.ui.theme.Primary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

private const val TAG = "CameraArea"

@Composable
fun CameraArea(
    currentView: CaptureView,
    onPoseDetected: (List<PostureAnalyzer.Point>) -> Unit,
    onCapture: (Bitmap, List<PostureAnalyzer.Point>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var poseLandmarks by remember { mutableStateOf<List<PostureAnalyzer.Point>>(emptyList()) }
    var statusMessage by remember { mutableStateOf("准备就绪") }
    val imageCaptureRef = remember { mutableStateOf<ImageCapture?>(null) }
    val isProcessing = remember { mutableStateOf(false) }

    val poseDetector = remember { PoseDetector(context) }
    val scope = rememberCoroutineScope()
    var cameraReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        poseDetector.setup()
    }

    // 主动轮询：每 200ms 读取一次最近的关键点结果
    LaunchedEffect(poseDetector) {
        while (true) {
            val points = poseDetector.toAnalyzerPoints()
            if (points != poseLandmarks) {
                poseLandmarks = points
                onPoseDetected(points)
                statusMessage = when {
                    points.isEmpty() -> "🔍 寻找人体…"
                    isFullBodyVisible(points) -> "✓ 全身入镜，可以拍照"
                    else -> "⚠️ 请确保头顶到脚底都在画面内"
                }
            }
            kotlinx.coroutines.delay(200)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            poseDetector.close()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1E293B))
                .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    imageCaptureRef.value = startCamera(
                        ctx, lifecycleOwner, previewView, poseDetector
                    ) { realCapture ->
                        // 真正的 ImageCapture 绑定好了才允许拍照
                        imageCaptureRef.value = realCapture
                        cameraReady = true
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            PoseOverlay(landmarks = poseLandmarks, modifier = Modifier.fillMaxSize())

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusMessage,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.size(56.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (cameraReady && !isProcessing.value) Color.White else Color.LightGray)
                    .border(4.dp, Primary, CircleShape)
                    .clickable(enabled = cameraReady && !isProcessing.value) {
                        val capture = imageCaptureRef.value
                        if (capture == null || isProcessing.value) return@clickable
                        isProcessing.value = true
                        statusMessage = "📸 正在分析…"
                        capture.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val bitmap = imageProxyToBitmap(image)
                                    image.close()
                                    if (bitmap == null) {
                                        isProcessing.value = false
                                        statusMessage = "⚠️ 拍照失败"
                                        return
                                    }
                                    // MediaPipe 推理是 CPU 密集（200-1000ms），必须丢到后台线程，
                                    // 不然会卡主线程，整个 UI 冻住，点 tab 都没反应
                                    scope.launch {
                                        val points = withContext(Dispatchers.Default) {
                                            poseDetector.detect(bitmap)
                                            poseDetector.toAnalyzerPoints()
                                        }
                                        // **总是保存照片**，只根据人体检测情况给提示
                                        onCapture(bitmap, points)
                                        statusMessage = when {
                                            points.isEmpty() -> "✓ ${currentView.displayName}已保存（未检测到人体，分析时可能用不上）"
                                            !isFullBodyVisible(points) -> "✓ ${currentView.displayName}已保存（人体不全，分析精度会下降）"
                                            else -> "✓ ${currentView.displayName}已拍摄"
                                        }
                                        isProcessing.value = false
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e(TAG, "Capture failed", exception)
                                    statusMessage = "⚠️ 拍照失败"
                                    isProcessing.value = false
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Primary)
                )
            }

            Box(modifier = Modifier.size(56.dp))
        }
    }
}

private fun startCamera(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    previewView: PreviewView,
    poseDetector: PoseDetector,
    onReady: (ImageCapture) -> Unit
): ImageCapture {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    var realImageCapture: ImageCapture? = null

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            realImageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                realImageCapture
            )
            // 通知 CameraArea 真正可用的 ImageCapture
            realImageCapture?.let { onReady(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Camera binding failed", e)
        }
    }, ContextCompat.getMainExecutor(context))

    // 先返回一个默认占位的，等真绑定好了再覆盖
    return ImageCapture.Builder().build()
}

@androidx.camera.core.ExperimentalGetImage
private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    // ImageCapture 默认输出 YUV_420_888，CameraX 1.3+ 提供了 toBitmap() 扩展
    // 内部用 ImageProxy 内部的 YUV→RGB 转换，比手写 plane 拼装稳得多
    val raw = image.toBitmap()
    val rotation = image.imageInfo.rotationDegrees
    return if (rotation != 0) {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)
    } else {
        raw
    }
}

private fun isFullBodyVisible(landmarks: List<PostureAnalyzer.Point>): Boolean {
    val indices = listOf(0, 23, 24, 25, 26, 27, 28)
    return indices.all { idx ->
        val p = landmarks.getOrNull(idx) ?: return false
        p.visibility > 0.5f
    }
}
