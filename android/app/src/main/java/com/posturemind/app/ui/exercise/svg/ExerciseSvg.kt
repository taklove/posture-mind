package com.posturemind.app.ui.exercise.svg

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * 简单的训练动作插画（用 Compose Canvas 绘制）
 *
 * 这是简化版，未来可以替换为真实视频 / 高级 SVG
 */
@Composable
fun ExerciseSvg(svgKey: String) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val cy = h / 2

        val skinColor = Color(0xFFFCD34D)
        val skinStroke = Color(0xFF92400E)
        val bodyColor = Color(0xFF0F766E)
        val accentColor = Color(0xFFF59E0B)
        val redColor = Color(0xFFEF4444)
        val greenColor = Color(0xFF10B981)

        when (svgKey) {
            "chinTuck" -> {
                // 头
                drawCircle(skinColor, 50f, Offset(cx - 30, cy - 60), stroke = skinStroke)
                // 脖子
                drawLine(skinColor, Offset(cx, cy - 30), Offset(cx, cy), strokeWidth = 12f)
                // 躯干
                drawRect(bodyColor, Offset(cx - 25, cy), Size(50f, 100f))
                // 箭头（往内收）
                drawArrow(Offset(cx + 50, cy - 70), Offset(cx + 110, cy - 70), redColor)
            }
            "headLift" -> {
                // 地面
                drawLine(Color(0xFF64748B), Offset(20f, h - 20), Offset(w - 20, h - 20), strokeWidth = 3f)
                // 仰卧身体
                drawOval(bodyColor, Offset(cx, cy + 30), Size(160f, 30f))
                // 头抬起
                drawCircle(skinColor, 22f, Offset(cx, cy), stroke = skinStroke)
                // 弯腿
                drawLine(skinStroke, Offset(cx + 60, cy + 30), Offset(cx + 90, cy - 10), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx + 90, cy - 10), Offset(cx + 90, cy + 80), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx - 60, cy + 30), Offset(cx - 90, cy - 10), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx - 90, cy - 10), Offset(cx - 90, cy + 80), strokeWidth = 5f)
            }
            "proneY" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                // 俯卧身体
                drawOval(bodyColor, Offset(cx, cy + 80), Size(200f, 20f))
                // 头
                drawCircle(skinColor, 18f, Offset(cx - 90, cy + 70), stroke = skinStroke)
                // Y 形手臂
                drawLine(accentColor, Offset(cx - 75, cy + 80), Offset(cx - 130, cy - 30), strokeWidth = 8f)
                drawLine(accentColor, Offset(cx - 75, cy + 80), Offset(cx - 20, cy - 30), strokeWidth = 8f)
            }
            "wallSlide" -> {
                // 墙
                drawLine(Color(0xFF64748B), Offset(80f, 20f), Offset(80f, h - 20), strokeWidth = 3f)
                // 头
                drawCircle(skinColor, 20f, Offset(cx - 30, cy - 60), stroke = skinStroke)
                // 身体
                drawRect(bodyColor, Offset(cx - 40, cy - 40), Size(20f, 100f))
                // 手臂
                drawLine(accentColor, Offset(cx - 30, cy - 30), Offset(100f, cy), strokeWidth = 8f)
                drawLine(accentColor, Offset(100f, cy), Offset(100f, cy + 40), strokeWidth = 8f)
                // 腿
                drawLine(skinStroke, Offset(cx - 35, cy + 60), Offset(cx - 40, h - 20), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx - 25, cy + 60), Offset(cx - 20, h - 20), strokeWidth = 5f)
            }
            "scapPushup" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                // 俯撑
                drawLine(bodyColor, Offset(40f, h - 30), Offset(w - 60, cy - 20), strokeWidth = 16f)
                drawCircle(skinColor, 14f, Offset(40f, h - 30), stroke = skinStroke)
                drawLine(skinStroke, Offset(40f, h - 30), Offset(60f, h - 50), strokeWidth = 5f)
                // 肩胛骨标记
                drawCircle(redColor, 8f, Offset(cx, cy - 30), style = Stroke(2f))
            }
            "bridge", "singleLegBridge" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                // 弯腿
                drawLine(skinStroke, Offset(cx - 80, h - 30), Offset(cx - 80, cy + 40), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx - 80, cy + 40), Offset(cx - 30, cy + 40), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx - 30, cy + 40), Offset(cx - 30, h - 30), strokeWidth = 5f)
                // 拱起身体
                drawArc(bodyColor, Offset(cx - 60, cy - 20), Size(140f, 80f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 16f)
                // 头
                drawCircle(skinColor, 14f, Offset(cx + 80, cy + 50), stroke = skinStroke)
                if (svgKey == "singleLegBridge") {
                    // 伸直的腿
                    drawLine(bodyColor, Offset(cx - 30, cy + 40), Offset(cx - 100, cy - 60), strokeWidth = 8f)
                }
                // 臀大肌
                drawCircle(redColor, 12f, Offset(cx - 40, cy), style = Stroke(2f))
            }
            "clamshell" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                drawOval(bodyColor, Offset(cx, cy + 30), Size(160f, 24f))
                drawCircle(skinColor, 18f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                // 弯曲的腿
                drawLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 80, cy - 30), strokeWidth = 6f)
                drawLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 80, cy + 90), strokeWidth = 6f)
            }
            "sideLegRaise" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                drawOval(bodyColor, Offset(cx, cy + 30), Size(160f, 24f))
                drawCircle(skinColor, 18f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                // 抬起的腿
                drawLine(bodyColor, Offset(cx + 60, cy + 30), Offset(cx + 130, cy - 50), strokeWidth = 8f)
            }
            "deadBug" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                drawOval(bodyColor, Offset(cx, cy + 30), Size(180f, 24f))
                drawCircle(skinColor, 14f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                // 伸出的手
                drawLine(skinStroke, Offset(cx - 80, cy + 25), Offset(cx - 150, cy - 10), strokeWidth = 5f)
                // 90° 腿
                drawLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 50, cy - 20), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx + 50, cy - 20), Offset(cx + 130, cy - 20), strokeWidth = 5f)
            }
            "birdDog" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                // 身体
                drawLine(bodyColor, Offset(60f, h - 30), Offset(cx, cy - 10), strokeWidth = 16f)
                // 头
                drawCircle(skinColor, 14f, Offset(60f, h - 30), stroke = skinStroke)
                // 支撑手
                drawLine(skinStroke, Offset(60f, h - 30), Offset(40f, h - 60), strokeWidth = 5f)
                // 支撑膝
                drawLine(skinStroke, Offset(cx, cy - 10), Offset(cx + 20, h - 30), strokeWidth = 5f)
                // 伸出对侧
                drawLine(accentColor, Offset(60f, h - 30), Offset(20f, cy - 70), strokeWidth = 6f)
                drawLine(accentColor, Offset(cx + 20, h - 30), Offset(cx + 130, cy - 80), strokeWidth = 6f)
            }
            "breathing" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                drawOval(bodyColor, Offset(cx, cy + 30), Size(180f, 24f))
                drawCircle(skinColor, 14f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                drawLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 50, cy - 20), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx + 50, cy - 20), Offset(cx + 130, cy - 20), strokeWidth = 5f)
                // 呼吸波
                drawArc(greenColor, Offset(cx - 50, cy - 60), Size(100f, 30f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 3f)
            }
            "tke" -> {
                // 弹力带
                drawLine(redColor, Offset(20f, cy), Offset(cx, cy), strokeWidth = 5f)
                // 头/躯干/腿（简化）
                drawCircle(skinColor, 14f, Offset(cx + 50, cy - 80), stroke = skinStroke)
                drawLine(bodyColor, Offset(cx + 50, cy - 60), Offset(cx + 50, cy + 30), strokeWidth = 16f)
                drawLine(bodyColor, Offset(cx + 50, cy + 30), Offset(cx + 50, cy + 130), strokeWidth = 16f)
                // 膝盖
                drawCircle(accentColor, 12f, Offset(cx, cy))
                // 箭头
                drawArrow(Offset(cx - 30, cy), Offset(cx + 10, cy), greenColor)
            }
            "shortFoot" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                drawOval(skinColor, Offset(cx, h - 50), Size(160f, 36f), stroke = skinStroke)
                // 足弓
                drawArc(redColor, Offset(cx - 30, h - 80), Size(60f, 50f), startAngle = 0f, sweepAngle = 180f, strokeWidth = 2f)
                drawArrow(Offset(cx, h - 50), Offset(cx, h - 100), greenColor)
            }
            "bridgeCurl" -> {
                drawLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                drawLine(skinStroke, Offset(cx - 80, h - 30), Offset(cx - 80, cy + 40), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx - 80, cy + 40), Offset(cx - 30, cy + 40), strokeWidth = 5f)
                drawLine(skinStroke, Offset(cx - 30, cy + 40), Offset(cx - 30, h - 30), strokeWidth = 5f)
                drawArc(bodyColor, Offset(cx - 60, cy - 20), Size(140f, 80f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 16f)
                drawCircle(skinColor, 14f, Offset(cx + 80, cy + 50), stroke = skinStroke)
                // 球
                drawCircle(accentColor, 30f, Offset(cx + 130, h - 30))
                drawLine(bodyColor, Offset(cx - 30, cy + 40), Offset(cx + 130, h - 50), strokeWidth = 8f)
            }
            else -> {
                // 通用：人形剪影
                drawCircle(skinColor, 25f, Offset(cx, cy - 70), stroke = skinStroke)
                drawRect(bodyColor, Offset(cx - 20, cy - 40), Size(40f, 100f))
                drawLine(skinStroke, Offset(cx - 20, cy - 30), Offset(cx - 50, cy + 30), strokeWidth = 8f)
                drawLine(skinStroke, Offset(cx + 20, cy - 30), Offset(cx + 50, cy + 30), strokeWidth = 8f)
                drawLine(skinStroke, Offset(cx - 10, cy + 60), Offset(cx - 15, h - 30), strokeWidth = 8f)
                drawLine(skinStroke, Offset(cx + 10, cy + 60), Offset(cx + 15, h - 30), strokeWidth = 8f)
            }
        }
    }
}

// 辅助绘制函数
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCircle(
    color: Color, radius: Float, center: Offset, style: Stroke = Stroke(),
    stroke: Color? = null
) {
    if (stroke != null) {
        drawCircle(stroke, radius, center, style = Stroke(3f))
    }
    drawCircle(color, radius, center, style = style)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRect(
    color: Color, topLeft: Offset, size: Size
) {
    drawRect(color, topLeft, size)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOval(
    color: Color, topLeft: Offset, size: Size, stroke: Color? = null
) {
    if (stroke != null) {
        drawOval(stroke, topLeft, size, style = Stroke(3f))
    }
    drawOval(color, topLeft, size)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLine(
    color: Color, start: Offset, end: Offset, strokeWidth: Float = 3f
) {
    drawLine(color, start, end, strokeWidth = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArc(
    color: Color, topLeft: Offset, size: Size, startAngle: Float, sweepAngle: Float, strokeWidth: Float = 3f
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = size,
        style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArrow(from: Offset, to: Offset, color: Color) {
    drawLine(color, from, to, strokeWidth = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    // 简单箭头头部
    val dx = to.x - from.x
    val dy = to.y - from.y
    val len = kotlin.math.hypot(dx, dy)
    if (len > 0) {
        val ux = dx / len
        val uy = dy / len
        val headLen = 12f
        val head1 = Offset(to.x - ux * headLen - uy * 8f, to.y - uy * headLen + ux * 8f)
        val head2 = Offset(to.x - ux * headLen + uy * 8f, to.y - uy * headLen - ux * 8f)
        drawLine(color, to, head1, strokeWidth = 3f)
        drawLine(color, to, head2, strokeWidth = 3f)
    }
}
