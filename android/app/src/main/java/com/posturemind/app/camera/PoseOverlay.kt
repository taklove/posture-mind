package com.posturemind.app.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.posturemind.app.data.PostureAnalyzer

/**
 * 在相机预览上叠加骨骼
 */
@Composable
fun PoseOverlay(
    landmarks: List<PostureAnalyzer.Point>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        if (landmarks.isEmpty()) return@Canvas

        // 骨骼连接
        val connections = listOf(
            // 躯干
            11 to 12, 11 to 23, 12 to 24, 23 to 24,
            // 头颈
            7 to 11, 8 to 12, 0 to 7, 0 to 8,
            // 左臂
            11 to 13, 13 to 15,
            // 右臂
            12 to 14, 14 to 16,
            // 左腿
            23 to 25, 25 to 27, 27 to 29, 29 to 31, 27 to 31,
            // 右腿
            24 to 26, 26 to 28, 28 to 30, 30 to 32, 28 to 32
        )

        val lineColor = Color(0xFF0F766E)
        val dotColor = Color(0xFFF59E0B)
        val width = size.width
        val height = size.height

        // 画骨骼
        connections.forEach { (a, b) ->
            val pa = landmarks.getOrNull(a) ?: return@forEach
            val pb = landmarks.getOrNull(b) ?: return@forEach
            if (pa.visibility < 0.5f || pb.visibility < 0.5f) return@forEach

            drawLine(
                color = lineColor,
                start = Offset(pa.x * width, pa.y * height),
                end = Offset(pb.x * width, pb.y * height),
                strokeWidth = 8f,
                cap = StrokeCap.Round
            )
        }

        // 画关键点
        val keyPoints = listOf(0, 7, 8, 11, 12, 13, 14, 15, 16, 23, 24, 25, 26, 27, 28)
        keyPoints.forEach { idx ->
            val p = landmarks.getOrNull(idx) ?: return@forEach
            if (p.visibility < 0.5f) return@forEach

            drawCircle(
                color = dotColor,
                radius = 10f,
                center = Offset(p.x * width, p.y * height)
            )
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = Offset(p.x * width, p.y * height)
            )
        }
    }
}
