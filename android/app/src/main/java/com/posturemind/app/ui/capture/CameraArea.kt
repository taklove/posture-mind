package com.posturemind.app.ui.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
                .height(500.dp)
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
                    imageCaptureRef.value = startCamera(ctx, lifecycleOwner, previewView, poseDetector)
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
                    .background(Color.White)
                    .border(4.dp, Primary, CircleShape)
                    .clickable(enabled = !isProcessing.value) {
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
                                    // 运行 MediaPipe
                                    poseDetector.detect(bitmap)
                                    val points = poseDetector.toAnalyzerPoints()
                                    if (points.isNotEmpty() && isFullBodyVisible(points)) {
                                        onCapture(bitmap, points)
                                        statusMessage = "✓ ${currentView.displayName}已拍摄"
                                    } else {
                                        statusMessage = "⚠️ 未检测到完整人体"
                                    }
                                    isProcessing.value = false
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
    poseDetector: PoseDetector
): ImageCapture {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    var imageCapture: ImageCapture? = null

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e(TAG, "Camera binding failed", e)
        }
    }, ContextCompat.getMainExecutor(context))

    return imageCapture ?: ImageCapture.Builder().build()
}

@androidx.camera.core.ExperimentalGetImage
private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null

    // 旋转
    val rotation = image.imageInfo.rotationDegrees
    return if (rotation != 0) {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}

private fun isFullBodyVisible(landmarks: List<PostureAnalyzer.Point>): Boolean {
    val indices = listOf(0, 23, 24, 25, 26, 27, 28)
    return indices.all { idx ->
        val p = landmarks.getOrNull(idx) ?: return false
        p.visibility > 0.5f
    }
}
