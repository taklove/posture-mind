package com.posturemind.app.ui.exercise.svg

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

/**
 * è®­ç»ƒåŠ¨ä½œæ’ç”»ï¼ˆå¸¦å¾ªçŽ¯åŠ¨ç”»ï¼‰
 *
 * progress 0â†’1â†’0ï¼ˆ2 ç§’ä¸€å‘¨æœŸï¼Œå¹³æ»‘å¾€è¿”ï¼‰ç”¨äºŽä½ç½®/è§’åº¦å˜åŒ–
 * pulse 0â†’1â†’0ï¼ˆ1.4 ç§’ä¸€å‘¨æœŸï¼Œçº¿æ€§ï¼‰ç”¨äºŽè„‰å†²/å‘¼å¸æ•ˆæžœ
 */
@Composable
fun ExerciseSvg(svgKey: String) {
    val transition = rememberInfiniteTransition()

    // ä½ç½®/è§’åº¦ç±»åŠ¨ç”»ï¼š2s å‘¨æœŸï¼Œease in-outï¼Œè‡ªåŠ¨åå‘ï¼ˆæ¥å›žä¸€æ¬¡ï¼‰
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // å‘¼å¸/è„‰å†²ï¼š1.4s å‘¨æœŸï¼Œlinear
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
                // å¤´å·¦å³æ»‘ï¼ˆæ°´å¹³æ–¹å‘ï¼‰ã€‚æ¥å›ž 30pxã€‚
                val dx = (progress - 0.5f) * 60f
                // å¤´
                pCircle(skinColor, 50f, Offset(cx - 30 + dx, cy - 60), stroke = skinStroke)
                // è„–å­
                pLine(skinColor, Offset(cx + dx, cy - 30), Offset(cx + dx, cy), strokeWidth = 12f)
                // èº¯å¹²ï¼ˆä¸åŠ¨ï¼‰
                pRect(bodyColor, Offset(cx - 25, cy), Size(50f, 100f))
                // ç®­å¤´ï¼ˆæ¥å›žæ»‘+å˜é€æ˜Žåº¦ï¼‰
                val arrowAlpha = (sin(progress * Math.PI.toFloat()) * 0.5f + 0.5f).coerceIn(0f, 1f)
                val arrowX = cx + 50 + dx * 0.5f
                pArrow(Offset(arrowX, cy - 70), Offset(arrowX + 60, cy - 70), redColor.copy(alpha = arrowAlpha))
            }
            "headLift" -> {
                // å¤´éƒ¨ä¸ŠæŠ¬ã€‚æ¥å›ž 25pxã€‚
                val dy = (progress - 0.5f) * -50f  // è´Ÿ = å‘ä¸Š
                // åœ°é¢
                pLine(Color(0xFF64748B), Offset(20f, h - 20), Offset(w - 20, h - 20), strokeWidth = 3f)
                // ä»°å§èº«ä½“ï¼ˆä¸åŠ¨ï¼‰
                pOval(bodyColor, Offset(cx, cy + 30), Size(160f, 30f))
                // å¤´æŠ¬èµ·
                pCircle(skinColor, 22f, Offset(cx, cy + dy), stroke = skinStroke)
                // å¼¯è…¿ï¼ˆä¸åŠ¨ï¼‰
                pLine(skinStroke, Offset(cx + 60, cy + 30), Offset(cx + 90, cy - 10), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 90, cy - 10), Offset(cx + 90, cy + 80), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 60, cy + 30), Offset(cx - 90, cy - 10), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 90, cy - 10), Offset(cx - 90, cy + 80), strokeWidth = 5f)
            }
            "proneY" -> {
                // Y å½¢æ‰‹è‡‚ï¼šåŒè‡‚ä»Žä¸­çº¿å‘ä¸Šä¸‹æ‰‡å¼€ã€‚æ¥å›ž 30pxã€‚
                val armSpread = progress * 30f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 80), Size(200f, 20f))
                pCircle(skinColor, 18f, Offset(cx - 90, cy + 70), stroke = skinStroke)
                // å·¦è‡‚å‘å¤–
                pLine(accentColor, Offset(cx - 75, cy + 80), Offset(cx - 130 - armSpread, cy - 30), strokeWidth = 8f)
                // å³è‡‚å‘å¤–
                pLine(accentColor, Offset(cx - 75, cy + 80), Offset(cx - 20 + armSpread, cy - 30), strokeWidth = 8f)
            }
            "wallSlide" -> {
                // é å¢™æ»‘ï¼šæ‰‹è‡‚æ²¿å¢™å‘ä¸Šæ»‘ 60px
                val armDy = -progress * 80f
                pLine(Color(0xFF64748B), Offset(80f, 20f), Offset(80f, h - 20), strokeWidth = 3f)
                pCircle(skinColor, 20f, Offset(cx - 30, cy - 60), stroke = skinStroke)
                pRect(bodyColor, Offset(cx - 40, cy - 40), Size(20f, 100f))
                // æ‰‹è‡‚å‘ä¸Šæ»‘
                pLine(accentColor, Offset(cx - 30, cy - 30 + armDy), Offset(100f, cy + armDy), strokeWidth = 8f)
                pLine(accentColor, Offset(100f, cy + armDy), Offset(100f, cy + 40 + armDy), strokeWidth = 8f)
                pLine(skinStroke, Offset(cx - 35, cy + 60), Offset(cx - 40, h - 20), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 25, cy + 60), Offset(cx - 20, h - 20), strokeWidth = 5f)
            }
            "scapPushup" -> {
                // è‚©èƒ›éª¨æ ‡è®°ä¸Šä¸‹èµ·ä¼
                val dy = -pulse * 12f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pLine(bodyColor, Offset(40f, h - 30), Offset(w - 60, cy - 20), strokeWidth = 16f)
                pCircle(skinColor, 14f, Offset(40f, h - 30), stroke = skinStroke)
                pLine(skinStroke, Offset(40f, h - 30), Offset(60f, h - 50), strokeWidth = 5f)
                // è‚©èƒ›éª¨è„‰åŠ¨
                val radius = 6f + pulse * 6f
                pCircle(redColor, radius, Offset(cx, cy - 30 + dy), style = Stroke(2f))
            }
            "bridge", "singleLegBridge" -> {
                // æ‹±èµ·ï¼šèº«ä½“å¼§çº¿ä¸Šä¸‹æ³¢åŠ¨
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
                // ä¸Šè…¿æ‰“å¼€/åˆä¸Š
                val legSpread = progress * 50f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(160f, 24f))
                pCircle(skinColor, 18f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 80 - legSpread, cy - 30), strokeWidth = 6f)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 80 - legSpread, cy + 90), strokeWidth = 6f)
            }
            "sideLegRaise" -> {
                // è…¿æŠ¬é«˜
                val legDy = -progress * 50f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(160f, 24f))
                pCircle(skinColor, 18f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(bodyColor, Offset(cx + 60, cy + 30), Offset(cx + 130, cy - 50 + legDy), strokeWidth = 8f)
            }
            "deadBug" -> {
                // å¯¹ä¾§æ‰‹è„šä¼¸å‡º/æ”¶å›ž
                val ext = (progress - 0.5f) * 60f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(180f, 24f))
                pCircle(skinColor, 14f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(skinStroke, Offset(cx - 80, cy + 25), Offset(cx - 150 - ext, cy - 10), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 50, cy - 20), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 50, cy - 20), Offset(cx + 130 + ext, cy - 20), strokeWidth = 5f)
            }
            "birdDog" -> {
                // å¯¹ä¾§æ‰‹è„šä¼¸å‡º/æ”¶å›ž
                val ext = (progress - 0.5f) * 50f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pLine(bodyColor, Offset(60f, h - 30), Offset(cx, cy - 10), strokeWidth = 16f)
                pCircle(skinColor, 14f, Offset(60f, h - 30), stroke = skinStroke)
                pLine(skinStroke, Offset(60f, h - 30), Offset(40f, h - 60), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx, cy - 10), Offset(cx + 20, h - 30), strokeWidth = 5f)
                // å¯¹ä¾§ä¼¸å‡º
                pLine(accentColor, Offset(60f, h - 30), Offset(20f - ext, cy - 70), strokeWidth = 6f)
                pLine(accentColor, Offset(cx + 20, h - 30), Offset(cx + 130 + ext, cy - 80), strokeWidth = 6f)
            }
            "breathing" -> {
                // å‘¼å¸ï¼šèƒ¸å£å¼§çº¿èƒ€ç¼©
                val breathSize = 180f + pulse * 30f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(bodyColor, Offset(cx, cy + 30), Size(breathSize, 24f))
                pCircle(skinColor, 14f, Offset(cx - 80, cy + 25), stroke = skinStroke)
                pLine(skinStroke, Offset(cx + 50, cy + 30), Offset(cx + 50, cy - 20), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx + 50, cy - 20), Offset(cx + 130, cy - 20), strokeWidth = 5f)
                // å‘¼å¸å¼§çº¿ç¼©æ”¾
                val arcW = 100f + pulse * 20f
                pArc(greenColor, Offset(cx - arcW / 2, cy - 60), Size(arcW, 30f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 3f)
            }
            "tke" -> {
                // è†ç›–ä¸Šä¸‹ç§»åŠ¨ï¼ˆä¼¸å±•/å¼¯æ›²ï¼‰
                val kneeDy = (progress - 0.5f) * 30f
                pLine(redColor, Offset(20f, cy), Offset(cx, cy), strokeWidth = 5f)
                pCircle(skinColor, 14f, Offset(cx + 50, cy - 80), stroke = skinStroke)
                pLine(bodyColor, Offset(cx + 50, cy - 60), Offset(cx + 50, cy + 30), strokeWidth = 16f)
                pLine(bodyColor, Offset(cx + 50, cy + 30), Offset(cx + 50, cy + 130), strokeWidth = 16f)
                // è†ç›–æ ‡è®°è„‰åŠ¨
                val radius = 10f + pulse * 6f
                pCircle(accentColor, radius, Offset(cx, cy + kneeDy))
                val arrowAlpha = (sin(progress * Math.PI.toFloat()) * 0.5f + 0.5f).coerceIn(0f, 1f)
                pArrow(Offset(cx - 30, cy + kneeDy), Offset(cx + 10, cy + kneeDy), greenColor.copy(alpha = arrowAlpha))
            }
            "shortFoot" -> {
                // è¶³å¼“ç¼©æ”¾
                val archH = 40f + pulse * 20f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pOval(skinColor, Offset(cx, h - 50), Size(160f, 36f), stroke = skinStroke)
                pArc(redColor, Offset(cx - 30, h - 80), Size(60f, archH), startAngle = 0f, sweepAngle = 180f, strokeWidth = 2f)
                pArrow(Offset(cx, h - 50), Offset(cx, h - 100), greenColor)
            }
            "bridgeCurl" -> {
                // çƒæ»šåŠ¨ + è…¿å¼¯
                val curlAngle = -progress * 35f
                pLine(Color(0xFF64748B), Offset(20f, h - 30), Offset(w - 20, h - 30), strokeWidth = 3f)
                pLine(skinStroke, Offset(cx - 80, h - 30), Offset(cx - 80, cy + 40), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 80, cy + 40), Offset(cx - 30, cy + 40), strokeWidth = 5f)
                pLine(skinStroke, Offset(cx - 30, cy + 40), Offset(cx - 30, h - 30), strokeWidth = 5f)
                pArc(bodyColor, Offset(cx - 60, cy - 20), Size(140f, 80f), startAngle = 180f, sweepAngle = 180f, strokeWidth = 16f)
                pCircle(skinColor, 14f, Offset(cx + 80, cy + 50), stroke = skinStroke)
                // çƒå¾€è„šæ–¹å‘æ»‘
                val ballX = cx + 80 + progress * 50f
                val ballY = h - 30 - (1f - kotlin.math.abs(progress - 0.5f) * 2f) * 8f
                pCircle(accentColor, 30f, Offset(ballX, ballY))
                pLine(bodyColor, Offset(cx - 30, cy + 40), Offset(ballX - 20, ballY - 20), strokeWidth = 8f)
                // é™é»˜ä½¿ç”¨ curlAngle é¿å…æœªç”¨è­¦å‘Š
                val _unused = curlAngle
            }
            else -> {
                // é€šç”¨ï¼šäººå½¢å‰ªå½±ï¼Œç¼“æ…¢æ‘†åŠ¨
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

// è¾…åŠ©ç»˜åˆ¶å‡½æ•°ï¼ˆç§æœ‰å‘½åé¿å…ä¸Ž Compose drawXxx é‡åï¼‰
private fun androidx.compose.ui.graphics.drawscope.DrawScope.pCircle(
    color: Color, radius: Float, center: Offset, style: Stroke = Stroke(),
    stroke: Color? = null
) {
    if (stroke != null) {
        pCircle(stroke, radius, center, style = Stroke(width = 3f))
    }
    pCircle(color, radius, center, style = style)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.pRect(
    color: Color, topLeft: Offset, size: Size
) {
    pRect(color, topLeft, size)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.pOval(
    color: Color, topLeft: Offset, size: Size, stroke: Color? = null
) {
    if (stroke != null) {
        pOval(stroke, topLeft, size, style = Stroke(width = 3f))
    }
    pOval(color, topLeft, size)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.pLine(
    color: Color, start: Offset, end: Offset, strokeWidth: Float = 3f
) {
    pLine(color, start, end, strokeWidth = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.pArc(
    color: Color, topLeft: Offset, size: Size, startAngle: Float, sweepAngle: Float, strokeWidth: Float = 3f
) {
    pArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = size,
        style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.pArrow(from: Offset, to: Offset, color: Color) {
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
