package com.posturemind.app.ui.capture

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.posturemind.app.camera.PoseOverlay
import com.posturemind.app.data.CaptureView
import com.posturemind.app.data.PostureAnalyzer
import com.posturemind.app.ui.theme.Primary
import com.posturemind.app.viewmodel.PostureViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

private const val TAG = "CaptureScreen"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CaptureScreen(
    viewModel: PostureViewModel,
    onAnalyze: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.assessment.collectAsState()

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "体态拍摄",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(48.dp))
        }

        Text(
            text = "📌 请按顺序拍摄 3 个角度",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
            textAlign = TextAlign.Center
        )

        // View tabs
        ViewTabs(
            current = state.currentView,
            captured = state.captured.keys,
            onSelect = { viewModel.setCurrentView(it) }
        )

        // 当前视角的拍摄提示
        Text(
            text = currentViewHint(state.currentView),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 2.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        if (cameraPermission.status.isGranted) {
            CameraArea(
                currentView = state.currentView,
                onPoseDetected = { landmarks -> /* no-op for preview */ },
                onCapture = { bitmap, landmarks ->
                    viewModel.saveCapturedFrame(state.currentView, bitmap, landmarks)
                    // 拍完一张后自动切到下一个未拍的角度
                    val captured = state.captured.keys + state.currentView
                    if (captured.size < 3) {
                        val next = CaptureView.entries.firstOrNull { it !in captured }
                        if (next != null) viewModel.setCurrentView(next)
                    }
                }
            )
        } else {
            PermissionPrompt(onRequest = { cameraPermission.launchPermissionRequest() })
        }

        Spacer(Modifier.height(16.dp))

        // 大"下一步"按钮：拍到 1-2 张时切下一张；拍齐 3 张时跳到分析
        val capturedCount = state.captured.size
        Button(
            onClick = {
                if (capturedCount == 3) {
                    onAnalyze()
                } else {
                    val captured = state.captured.keys
                    val next = CaptureView.entries.firstOrNull { it !in captured }
                    if (next != null) viewModel.setCurrentView(next)
                }
            },
            enabled = capturedCount > 0,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (capturedCount == 3) Primary else Primary.copy(alpha = 0.85f)
            )
        ) {
            Text(
                when {
                    capturedCount == 0 -> "👆 拍下面板上的白圈开始"
                    capturedCount < 3 -> "下一张（${capturedCount}/3）→"
                    else -> "🔍 分析 3 张照片"
                },
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun currentViewHint(view: CaptureView): String = when (view) {
    CaptureView.FRONT -> "👤 面对镜头，双手自然下垂"
    CaptureView.SIDE -> "👤 侧面站立，看不到的那侧贴镜头都行"
    CaptureView.BACK -> "👤 背对镜头，双手自然下垂"
}

@Composable
private fun ViewTabs(
    current: CaptureView,
    captured: Set<CaptureView>,
    onSelect: (CaptureView) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CaptureView.entries.forEach { view ->
            val isActive = view == current
            val isCaptured = view in captured
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            isActive -> Primary
                            isCaptured -> Primary.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (isActive) Primary else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(view) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = view.displayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    if (isCaptured) {
                        Spacer(Modifier.size(4.dp))
                        Text("✓", fontSize = 14.sp, color = if (isActive) Color.White else Primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionPrompt(onRequest: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📷", fontSize = 48.sp)
            Spacer(Modifier.height(12.dp))
            Text("需要相机权限才能评估体态", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRequest) {
                Text("授权相机")
            }
        }
    }
}
