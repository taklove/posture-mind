package com.posturemind.app.ui.exercise.svg

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * 训练动作插画（带循环动画）
 *
 * progress 0→1→0（2 秒一周期，平滑往返）用于位置/角度变化
 * pulse 0→1→0（1.4 秒一周期，线性）用于脉动/呼吸效果
 */
@Composable
fun ExerciseSvg(svgKey: String) {
    val transition = rememberInfiniteTransition()

    // 位置/角度类动画：2s 周期，ease in-out，自动反向（来回一次）
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 呼吸/脉动：1.4s 周期，linear
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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
                // 头左右滑（水平方向）。来回 60px。
                val dx = (progress - 0.5f) * 60f
                // 头
                pCircle(skinColor, 50f, Offset(cx - 30 + dx, cy - 60), stroke = skinStroke)
                // 脖子
                pLine(skinColor, Offset(cx + dx, cy - 30), Offset(cx + dx, cy), strokeWidth = 12f)
                // 躯干（不动）
                pRect(bodyColor, Offset(cx - 25, cy), Size(50f, 100f))
                // 箭头（来回滑+变透明度）
                val arrowAlpha = (sin(progress * Math.PI.toFloat()) * 0.5f + 0.5f).coerceIn(0f, 1f)
                val arrowX = cx + 50 + dx * 0.5f
                pArrow(Offset(arrowX, cy - 70), Offset(arrowX + 60, cy - 70), redColor.copy(alpha = arrowAlpha))
            }
            "headLift" -> {
                // 头部上抬。来回 50px。
                val dy = (progress - 0.5f) * -50f  // 负 = 向上
                // 地面
                pLine(Color(0xFF64748B), Offset(20f, h - 20), Offset(w - 20, h - 20), strokeWidth = 3f)
                // 仰卧身体（不动）
                pOval(bodyColor, Offset(cx, cy + 30), Size(160f, 30f))
                // 头抬起
                pCircle(skinColor, 22f, Offset(cx, cy + dy), stroke = skinStroke)
                // 弯腿（不动）
                pLine(skinStroke, Offset(cx + 60, cy + 30), Offset(cx + 90, cy - 10), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 90, cy - 10), Offset(cx + 90, cy + 80), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 60, cy + 30), Offset(cx - 90, cy - 10), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 90, cy - 10), Offset(cx - 90, cy + 80), strokeWidth = 5f)
            }
            "proneY" -> {
                // Y 形手臂：双臂从中线向上下甩开。来回 30px。
                val armSpread = progress * 30f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 80), Size(200f, 20f))
                pCircle(skinColor, 18f, Offset(cx - 90, cy + 70), stroke = skinStroke)
                // 左臂向外
                pLine(accentColor, Offset(cx - 75, cy + 80), Offset(cx - 130 - armSpread, cy - 30), strokeWidth = 8f)
                // 右臂向外
                pLine(accentColor, Offset(cx - 75, cy + 80), Offset(cx - 20 + armSpread, cy - 30), strokeWidth = 8f)
            }
            "wallSlide" -> {
                // 靠墙滑：手臂沿墙向上滑 80px
                val armDy = -progress * 80f
                pLine(Color(0xFF64748B), Offset(80f, 20f), Offset(80f, h - 20), strokeWidth = 3f)
                pCircle(skinColor, 20f, Offset(cx - 30, cy - 60), stroke = skinStroke)
                pRect(bodyColor, Offset(cx - 40, cy - 40), Size(20f, 100f))
                // 手臂向上滑
                pLine(accentColor, Offset(cx - 30, cy - 30 + armDy), Offset(100f, cy + armDy), strokeWidth = 8f)
                pLine(accentColor, Offset(100f, cy + armDy), Offset(100f, cy + 40 + armDy), strokeWidth = 8f)
                pLine(skinStroke, Offset(cx - 35, cy + 60), Offset(cx - 40, h - 20), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 25, cy + 60), Offset(cx - 20, h - 20), strokeWidth = 5f)
            }
            "scapPushup" -> {
                // 肩胛骨标记上下起伏
                val dy = -pulse * 12f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pLine(bodyColor, Offset(40f, h - 30), Offset(w - 60, cy - 20), strokeWidth = 16f)
                pCircle(skinColor, 14f, Offset(40f, h - 30), stroke = skinStroke)
                pLine(skinStroke, Offset(40f, h - 30), Offset(60f, h - 50), strokeWidth = 5f)
                // 肩胛骨脉动
                val radius = 6f + pulse * 6f
                pCircle(redColor, radius, Offset(cx, cy - 30 + dy), style = Stroke(2f))
            }
            "bridge", "singleLegBridge" -> {
                // 拱起：身体弧线上下波动
                val archDy = -pulse * 18f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pLine(skinStroke, Offset(cx - 80, h - 30), Offset(cx - 80, cy + 40), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 80, cy + 40), Offset(cx - 30, cy + 40), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 30, cy + 40), Offset(cx - 30, h - 30), strokeWidth = 5f)
                pArc(bodyColor, Offset(cx - 60, cy - 20 + archDy), Size(140f, 80f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 16f)
                pCircle(skinColor, 14f, Offset(cx + 80, cy + 50), stroke = skinStroke)
                if (svgKey == "singleLegBridge") {
                    pLine(bodyColor, Offset(cx - 30, cy + 40), Offset(cx - 100, cy - 60), strokeWidth = 8f)
                }
                pCircle(redColor, 12f, Offset(cx - 40, cy), style = Stroke(2f))
            }
            "clamshell" -> {
                // 上腿打开/合上
                val legSpread = progress * 50f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(160f, 24f))
                pCircle(skinColor, 18f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 80 - legSpread, cy - 30), strokeWidth = 6f)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 80 - legSpread, cy + 90), strokeWidth = 6f)
            }
            "sideLegRaise" -> {
                // 腿抬高
                val legDy = -progress * 50f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(160f, 24f))
                pCircle(skinColor, 18f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(bodyColor, Offset(cx + 60, cy + 30), Offset(cx + 130, cy - 50 + legDy), strokeWidth = 8f)
            }
            "deadBug" -> {
                // 对侧手足伸出/收回
                val ext = (progress - 0.5f) * 60f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(180f, 24f))
                pCircle(skinColor, 14f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(skinStroke, Offset(cx - 80, cy + 25), Offset(cx - 150 - ext, cy - 10), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 50, cy - 20), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 50, cy - 20), Offset(cx + 130 + ext, cy - 20), strokeWidth = 5f)
            }
            "birdDog" -> {
                // 对侧手足伸出/收回
                val ext = (progress - 0.5f) * 50f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pLine(bodyColor, Offset(60f, h - 30), Offset(cx, cy - 10), strokeWidth = 16f)
                pCircle(skinColor, 14f, Offset(60f, h - 30), stroke = skinStroke)
                pLine(skinStroke, Offset(60f, h - 30), Offset(40f, h - 60), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx, cy - 10), Offset(cx + 20, h - 30), strokeWidth = 5f)
                // 对侧伸出
                pLine(accentColor, Offset(60f, h - 30), Offset(20f - ext, cy - 70), strokeWidth = 6f)
                pLine(accentColor, Offset(cx + 20, h - 30), Offset(cx + 130 + ext, cy - 80), strokeWidth = 6f)
            }
            "breathing" -> {
                // 呼吸：胸口弧线胀缩
                val breathSize = 180f + pulse * 30f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(breathSize, 24f))
                pCircle(skinColor, 14f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 50, cy - 20), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 50, cy - 20), Offset(cx + 130, cy - 20), strokeWidth = 5f)
                // 呼吸弧线缩放
                val arcW = 100f + pulse * 20f
                pArc(greenColor, Offset(cx - arcW / 2, cy - 60), Size(arcW, 30f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 3f)
            }
            "tke" -> {
                // 膝盖上下移动（伸展/弯曲）
                val kneeDy = (progress - 0.5f) * 30f
                pLine(redColor, Offset(20f, cy), Offset(cx, cy), strokeWidth = 5f)
                pCircle(skinColor, 14f, Offset(cx + 50, cy - 80), stroke = skinStroke)
                pLine(bodyColor, Offset(cx + 50, cy - 60), Offset(cx + 50, cy + 30), strokeWidth = 16f)
                pLine(bodyColor, Offset(cx + 50, cy + 30), Offset(cx + 50, cy + 130), strokeWidth = 16f)
                // 膝盖标记脉动
                val radius = 10f + pulse * 6f
                pCircle(accentColor, radius, Offset(cx, cy + kneeDy))
                val arrowAlpha = (sin(progress * Math.PI.toFloat()) * 0.5f + 0.5f).coerceIn(0f, 1f)
                pArrow(Offset(cx - 30, cy + kneeDy), Offset(cx + 10, cy + kneeDy), greenColor.copy(alpha = arrowAlpha))
            }
            "shortFoot" -> {
                // 足弓缩放
                val archH = 40f + pulse * 20f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(skinColor, Offset(cx, h - 50), Size(160f, 36f), stroke = skinStroke)
                pArc(redColor, Offset(cx - 30, h - 80), Size(60f, archH), startAngle = 0f, sweepAngle = 180f, strokeWidth = 2f)
                pArrow(Offset(cx, h - 50), Offset(cx, h - 100), greenColor)
            }
            "bridgeCurl" -> {
                // 球滚动 + 腿弯
                val curlAngle = -progress * 35f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pLine(skinStroke, Offset(cx - 80, h - 30), Offset(cx - 80, cy + 40), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 80, cy + 40), Offset(cx - 30, cy + 40), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 30, cy + 40), Offset(cx - 30, h - 30), strokeWidth = 5f)
                pArc(bodyColor, Offset(cx - 60, cy - 20), Size(140f, 80f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 16f)
                pCircle(skinColor, 14f, Offset(cx + 80, cy + 50), stroke = skinStroke)
                // 球往脚方向滑
                val ballX = cx + 80 + progress * 50f
                val ballY = h - 30 - (1f - kotlin.math.abs(progress - 0.5f) * 2f) * 8f
                pCircle(accentColor, 30f, Offset(ballX, ballY))
                pLine(bodyColor, Offset(cx - 30, cy + 40), Offset(ballX - 20, ballY - 20), strokeWidth = 8f)
                // 静默使用 curlAngle 避免未用警告
                val _unused = curlAngle
            }
            else -> {
                // 通用：人形剪影，缓慢摆动
                val sway = sin(progress * Math.PI.toFloat()) * 8f
                pCircle(skinColor, 25f, Offset(cx + sway, cy - 70), stroke = skinStroke)
                pRect(bodyColor, Offset(cx - 20, cy - 40), Size(40f, 100f))
                pLine(skinStroke, Offset(cx - 20, cy - 30), Offset(cx - 50 + sway * 0.5f, cy + 30), strokeWidth = 8f)
                pLine(skinStroke, Offset(cx + 20, cy - 30), Offset(cx + 50 - sway * 0.5f, cy + 30), strokeWidth = 8f)
                pLine(skinStroke, Offset(cx - 10, cy + 60), Offset(cx - 15, h - 30), strokeWidth = 8f)
                pLine(skinStroke, Offset(cx + 10, cy + 60), Offset(cx + 15, h - 30), strokeWidth = 8f)
            }
        }
    }
}

// 辅助绘制函数（私有命名 pXxx 避免与 DrawScope.drawXxx 同名；内部调用真正的 drawXxx）
private fun DrawScope.pCircle(
    color: Color,
    radius: Float,
    center: Offset,
    style: Stroke = Stroke(),
    stroke: Color? = null
) {
    if (stroke != null) {
        // 先画一个 stroke 描边
        drawCircle(color = stroke, radius = radius, center = center, style = Stroke(width = 3f))
    }
    drawCircle(color = color, radius = radius, center = center, style = style)
}

private fun DrawScope.pRect(
    color: Color,
    topLeft: Offset,
    size: Size
) {
    drawRect(color = color, topLeft = topLeft, size = size)
}

private fun DrawScope.pOval(
    color: Color,
    topLeft: Offset,
    size: Size,
    stroke: Color? = null
) {
    if (stroke != null) {
        drawOval(color = stroke, topLeft = topLeft, size = size, style = Stroke(width = 3f))
    }
    drawOval(color = color, topLeft = topLeft, size = size)
}

private fun DrawScope.pLine(
    color: Color,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 3f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

private fun DrawScope.pArc(
    color: Color,
    topLeft: Offset,
    size: Size,
    startAngle: Float,
    sweepAngle: Float,
    strokeWidth: Float = 3f
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

private fun DrawScope.pArrow(from: Offset, to: Offset, color: Color) {
    pLine(color, from, to, strokeWidth = 4f)
    val dx = to.x - from.x
    val dy = to.y - from.y
    val len = kotlin.math.hypot(dx, dy)
    if (len > 0) {
        val ux = dx / len
        val uy = dy / len
        val headLen = 12f
        val head1 = Offset(to.x - ux * headLen - uy * 8f, to.y - uy * headLen + ux * 8f)
        val head2 = Offset(to.x - ux * headLen + uy * 8f, to.y - uy * headLen - ux * 8f)
        pLine(color, to, head1, strokeWidth = 3f)
        pLine(color, to, head2, strokeWidth = 3f)
    }
}
