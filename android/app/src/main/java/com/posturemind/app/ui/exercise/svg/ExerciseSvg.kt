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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.sin

// =====================================================
// 颜色
// =====================================================
private val SKIN = Color(0xFFFCD34D)         // 头/颈/手/脚
private val SKIN_DARK = Color(0xFF92400E)    // 描边
private val BODY = Color(0xFF0F766E)         // 躯干/腿（衣服色）
private val BODY_LIGHT = Color(0xFF14B8A6)    // 衣服高光
private val ACCENT = Color(0xFFF59E0B)       // 动作箭头
private val MUSCLE = Color(0xFFEF4444)       // 主训肌群（红）
private val MUSCLE_2 = Color(0xFFF97316)     // 副训肌群（橙）
private val TEXT_DARK = Color(0xFF111827)    // 文字
private val GROUND = Color(0xFF64748B)       // 地面

// =====================================================
// 公共动画（每个 Composable 实例各自独立）
// =====================================================
// progress 0→1→0（2s 周期，往返）用于位置/角度变化
// pulse    0→1→0（1.4s 周期，线性）用于肌肉脉动

// =====================================================
// 主体：每个 svgKey 调一个绘图函数
// =====================================================
@Composable
fun ExerciseSvg(svgKey: String) {
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val pulse by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
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

        when (svgKey) {
            "chinTuck"        -> drawChinTuck(progress, pulse, cx, cy)
            "headLift"        -> drawHeadLift(progress, pulse, cx, cy)
            "proneY"          -> drawProneY(progress, pulse, cx, cy)
            "wallSlide"       -> drawWallSlide(progress, pulse, cx, cy)
            "scapPushup"      -> drawScapPushup(progress, pulse, cx, cy)
            "bridge"          -> drawBridge(progress, pulse, cx, cy, singleLeg = false)
            "singleLegBridge" -> drawBridge(progress, pulse, cx, cy, singleLeg = true)
            "clamshell"       -> drawClamshell(progress, pulse, cx, cy)
            "sideLegRaise"    -> drawSideLegRaise(progress, pulse, cx, cy)
            "deadBug"         -> drawDeadBug(progress, pulse, cx, cy)
            "birdDog"         -> drawBirdDog(progress, pulse, cx, cy)
            "breathing"       -> drawBreathing(progress, pulse, cx, cy)
            "tke"             -> drawTke(progress, pulse, cx, cy)
            "shortFoot"       -> drawShortFoot(progress, pulse, cx, cy)
            "bridgeCurl"      -> drawBridgeCurl(progress, pulse, cx, cy)
            else              -> drawDefault(progress, pulse, cx, cy)
        }
    }
}

// =====================================================
// 1. chin_tuck  下颌回收 —— 颈深屈肌
// =====================================================
private fun DrawScope.drawChinTuck(p: Float, pulse: Float, cx: Float, cy: Float) {
    val dx = (p - 0.5f) * 30f  // 头水平往回收

    // 躯干（肩膀 + 上背）
    pRoundRect(BODY, Offset(cx - 70, cy + 20), Size(140, 110), 16f)
    pRoundRect(BODY_LIGHT, Offset(cx - 70, cy + 20), Size(140, 30), 16f)
    // 脖子
    pRoundRect(SKIN, Offset(cx - 18 + dx, cy - 35), Size(36, 50), 8f)
    pRoundRect(SKIN_DARK.copy(alpha = 0.15f), Offset(cx - 18 + dx, cy - 35), Size(36, 50), 8f)
    // 头
    pCircle(SKIN, 36f, Offset(cx + dx, cy - 80), stroke = SKIN_DARK)
    // 头发
    pPath(SKIN_DARK, Offset(cx - 32 + dx, cy - 100), Size(64, 30), 16f)
    // 眼睛
    pCircle(Color.Black, 2f, Offset(cx + 12 + dx, cy - 80))

    // 主训肌群：颈深屈肌（脖子前面）
    pMuscle("颈深屈肌", Offset(cx - 12 + dx, cy - 15), Size(28, 36), MUSCLE, pulse)

    // 箭头
    val arrA = (sin(p * Math.PI.toFloat()) * 0.5f + 0.5f).coerceIn(0f, 1f)
    pArrow(Offset(cx + 50, cy - 80), Offset(cx + 50 - dx, cy - 80), ACCENT.copy(alpha = arrA))
}

// =====================================================
// 2. supine_head_lift  仰卧抬头 —— 颈深屈肌
// =====================================================
private fun DrawScope.drawHeadLift(p: Float, pulse: Float, cx: Float, cy: Float) {
    val dy = (p - 0.5f) * -40f  // 头往上抬

    // 地面
    pLine(GROUND, Offset(20f, cy + 90), Offset(800f, cy + 90), strokeWidth = 3f)

    // 仰卧身体（侧视）—— 躯干水平
    pRoundRect(BODY, Offset(cx - 110, cy + 10), Size(220, 60), 14f)
    pRoundRect(BODY_LIGHT, Offset(cx - 110, cy + 10), Size(220, 16), 14f)
    // 髋
    pCircle(BODY, 20f, Offset(cx + 50, cy + 40), stroke = SKIN_DARK)
    // 弯腿
    pThickLine(SKIN_DARK, Offset(cx + 50, cy + 50), Offset(cx + 100, cy + 20), 18f)
    pThickLine(SKIN_DARK, Offset(cx + 100, cy + 20), Offset(cx + 110, cy + 90), 18f)
    pThickLine(SKIN_DARK, Offset(cx + 50, cy + 50), Offset(cx + 30, cy + 90), 18f)
    pThickLine(SKIN_DARK, Offset(cx + 30, cy + 90), Offset(cx + 60, cy + 90), 14f)

    // 脖子（侧视）
    pRoundRect(SKIN, Offset(cx - 150, cy - 10 + dy), Size(40, 50), 10f)
    // 头
    pCircle(SKIN, 28f, Offset(cx - 165, cy - 25 + dy), stroke = SKIN_DARK)
    // 头发
    pPath(SKIN_DARK, Offset(cx - 180, cy - 50 + dy), Size(40, 28), 14f)

    // 主训肌群：颈深屈肌（脖子前面）
    pMuscle("颈深屈肌", Offset(cx - 130, cy - 5 + dy), Size(26, 32), MUSCLE, pulse)
}

// =====================================================
// 3. prone_y_raise  俯卧 Y 举 —— 下斜方肌
// =====================================================
private fun DrawScope.drawProneY(p: Float, pulse: Float, cx: Float, cy: Float) {
    val arm = p * 25f

    // 地面
    pLine(GROUND, Offset(20f, cy + 90), Offset(800f, cy + 90), strokeWidth = 3f)

    // 俯卧身体（侧视）—— 头在左
    pRoundRect(BODY, Offset(cx - 120, cy + 30), Size(220, 50), 14f)
    // 髋/腿
    pThickLine(BODY, Offset(cx + 100, cy + 55), Offset(cx + 160, cy + 80), 28f)
    pThickLine(SKIN_DARK, Offset(cx + 160, cy + 80), Offset(cx + 170, cy + 90), 16f)
    // 头（侧视）
    pCircle(SKIN, 26f, Offset(cx - 140, cy + 35), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 158, cy + 12), Size(36, 26), 12f)

    // 手臂 Y 形举起
    pThickLine(ACCENT, Offset(cx - 80, cy + 30), Offset(cx - 130 - arm, cy - 30), 18f)
    pThickLine(ACCENT, Offset(cx - 80, cy + 30), Offset(cx - 30 + arm, cy - 30), 18f)
    // 手
    pCircle(SKIN, 10f, Offset(cx - 130 - arm, cy - 30), stroke = SKIN_DARK)
    pCircle(SKIN, 10f, Offset(cx - 30 + arm, cy - 30), stroke = SKIN_DARK)

    // 主训肌群：下斜方肌（肩胛之间）
    pMuscle("下斜方肌", Offset(cx - 40, cy + 20), Size(60, 28), MUSCLE, pulse)
}

// =====================================================
// 4. wall_slide  靠墙滑举 —— 下斜方肌 + 前锯肌
// =====================================================
private fun DrawScope.drawWallSlide(p: Float, pulse: Float, cx: Float, cy: Float) {
    val dy = -p * 60f

    // 墙（左侧竖线）
    pLine(GROUND, Offset(cx - 100, 20f), Offset(cx - 100, 800f), strokeWidth = 4f)
    // 地面
    pLine(GROUND, Offset(20f, cy + 120), Offset(800f, cy + 120), strokeWidth = 3f)

    // 人体（侧视）—— 头靠墙
    // 头
    pCircle(SKIN, 30f, Offset(cx - 70, cy - 100), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 86, cy - 125), Size(40, 28), 14f)
    // 脖子 + 躯干
    pRoundRect(BODY, Offset(cx - 85, cy - 70), Size(40, 180), 12f)
    // 髋
    pRoundRect(BODY, Offset(cx - 90, cy + 90), Size(50, 30), 12f)
    // 腿
    pThickLine(BODY, Offset(cx - 65, cy + 120), Offset(cx - 65, cy + 200), 32f)
    pThickLine(BODY, Offset(cx - 40, cy + 120), Offset(cx - 40, cy + 200), 32f)
    pThickLine(SKIN_DARK, Offset(cx - 80, cy + 200), Offset(cx - 25, cy + 200), 18f)

    // 手臂（沿墙向上滑）
    pThickLine(ACCENT, Offset(cx - 70, cy - 40 + dy), Offset(cx - 100, cy + dy), 18f)
    pThickLine(ACCENT, Offset(cx - 100, cy + dy), Offset(cx - 100, cy + 30 + dy), 18f)
    pCircle(SKIN, 10f, Offset(cx - 100, cy + 30 + dy), stroke = SKIN_DARK)

    // 主训肌群：下斜方肌（肩胛之间）
    pMuscle("下斜方肌", Offset(cx - 30, cy - 30), Size(50, 24), MUSCLE, pulse)
    // 副训：前锯肌（肋侧）
    pMuscle("前锯肌", Offset(cx - 30, cy + 20), Size(40, 30), MUSCLE_2, pulse)
}

// =====================================================
// 5. scapular_pushup  肩胛俯卧撑 —— 前锯肌
// =====================================================
private fun DrawScope.drawScapPushup(p: Float, pulse: Float, cx: Float, cy: Float) {
    val dy = -pulse * 8f
    val radius = 6f + pulse * 5f

    // 地面
    pLine(GROUND, Offset(20f, cy + 90), Offset(800f, cy + 90), strokeWidth = 3f)

    // 平板支撑身体（侧视）—— 头在左
    pThickLine(BODY, Offset(cx - 100, cy + 50), Offset(cx + 90, cy - 20), 36f)
    // 头
    pCircle(SKIN, 22f, Offset(cx - 120, cy + 50), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 134, cy + 30), Size(28, 22), 11f)
    // 前臂撑地
    pThickLine(BODY, Offset(cx - 120, cy + 60), Offset(cx - 130, cy + 90), 18f)
    pThickLine(SKIN_DARK, Offset(cx - 130, cy + 90), Offset(cx - 100, cy + 90), 14f)
    // 脚
    pCircle(SKIN, 12f, Offset(cx + 90, cy - 20), stroke = SKIN_DARK)
    pThickLine(SKIN_DARK, Offset(cx + 90, cy - 10), Offset(cx + 100, cy + 90), 12f)

    // 主训肌群：前锯肌（肋侧，肩胛下方）
    pMuscle("前锯肌", Offset(cx + 10, cy - 5 + dy), Size(50, 32), MUSCLE, pulse)
    // 标记圆（肩胛骨）
    pCircle(MUSCLE_2, radius, Offset(cx - 20, cy + 10 + dy), style = Stroke(2f))
}

// =====================================================
// 6. glute_bridge  臀桥 —— 臀大肌 + 腘绳肌
// =====================================================
private fun DrawScope.drawBridge(p: Float, pulse: Float, cx: Float, cy: Float, singleLeg: Boolean) {
    val arch = -pulse * 28f  // 髋部上下

    // 地面
    pLine(GROUND, Offset(20f, cy + 110), Offset(800f, cy + 110), strokeWidth = 3f)

    // 头 + 肩（侧视，左侧）
    pCircle(SKIN, 26f, Offset(cx - 140, cy + 70), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 156, cy + 46), Size(34, 26), 13f)
    // 肩膀
    pRoundRect(BODY, Offset(cx - 110, cy + 60), Size(40, 30), 10f)

    // 躯干（拱起）—— 弧线
    pArc(BODY, Offset(cx - 70, cy - 20 + arch), Size(180, 100), 180f, 180f, strokeWidth = 36f)
    // 高光
    pArc(BODY_LIGHT, Offset(cx - 70, cy - 20 + arch), Size(180, 100), 180f, 180f, strokeWidth = 12f)

    // 髋
    pCircle(BODY, 22f, Offset(cx + 70, cy + 30 + arch), stroke = SKIN_DARK)
    // 弯腿
    pThickLine(SKIN_DARK, Offset(cx + 70, cy + 50 + arch), Offset(cx + 130, cy + 50), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 130, cy + 50), Offset(cx + 130, cy + 105), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 130, cy + 105), Offset(cx + 110, cy + 110), 12f)

    // 单腿桥 —— 一条腿抬起
    if (singleLeg) {
        pThickLine(BODY, Offset(cx + 70, cy + 50 + arch), Offset(cx + 160, cy - 30), 18f)
        pThickLine(SKIN_DARK, Offset(cx + 160, cy - 30), Offset(cx + 170, cy - 50), 12f)
    }

    // 主训肌群：臀大肌（髋后）
    pMuscle("臀大肌", Offset(cx + 50, cy + 20 + arch), Size(46, 38), MUSCLE, pulse)
    // 副训：腘绳肌（大腿后）
    pMuscle("腘绳肌", Offset(cx + 100, cy + 50 + arch / 2), Size(40, 22), MUSCLE_2, pulse)
}

// =====================================================
// 7. clamshell  蚌壳开合 —— 臀中肌
// =====================================================
private fun DrawScope.drawClamshell(p: Float, pulse: Float, cx: Float, cy: Float) {
    val legSpread = p * 50f

    // 地面
    pLine(GROUND, Offset(20f, cy + 90), Offset(800f, cy + 90), strokeWidth = 3f)

    // 侧卧身体（侧视）—— 头在左
    pCircle(SKIN, 24f, Offset(cx - 120, cy + 50), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 134, cy + 28), Size(30, 24), 12f)
    // 躯干（侧躺）—— 横放
    pRoundRect(BODY, Offset(cx - 90, cy + 20), Size(180, 50), 14f)
    pRoundRect(BODY_LIGHT, Offset(cx - 90, cy + 20), Size(180, 14), 14f)
    // 髋
    pCircle(BODY, 20f, Offset(cx + 90, cy + 50), stroke = SKIN_DARK)

    // 下方腿（不动）
    pThickLine(SKIN_DARK, Offset(cx + 90, cy + 60), Offset(cx + 150, cy + 20), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 150, cy + 20), Offset(cx + 150, cy + 90), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 150, cy + 90), Offset(cx + 130, cy + 90), 14f)
    // 上方腿（开合）
    pThickLine(SKIN_DARK, Offset(cx + 90, cy + 40), Offset(cx + 150 - legSpread, cy - 20), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 150 - legSpread, cy - 20), Offset(cx + 150 - legSpread, cy + 50), 22f)

    // 主训肌群：臀中肌（髋外侧）
    pMuscle("臀中肌", Offset(cx + 70, cy + 70), Size(34, 24), MUSCLE, pulse)
}

// =====================================================
// 8. side_leg_raise  侧卧抬腿 —— 臀中肌
// =====================================================
private fun DrawScope.drawSideLegRaise(p: Float, pulse: Float, cx: Float, cy: Float) {
    val legDy = -p * 50f

    // 地面
    pLine(GROUND, Offset(20f, cy + 90), Offset(800f, cy + 90), strokeWidth = 3f)

    // 侧卧身体（侧视）—— 头在左
    pCircle(SKIN, 24f, Offset(cx - 120, cy + 50), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 134, cy + 28), Size(30, 24), 12f)
    pRoundRect(BODY, Offset(cx - 90, cy + 20), Size(180, 50), 14f)
    pRoundRect(BODY_LIGHT, Offset(cx - 90, cy + 20), Size(180, 14), 14f)
    pCircle(BODY, 20f, Offset(cx + 90, cy + 50), stroke = SKIN_DARK)

    // 下方腿（不动）
    pThickLine(SKIN_DARK, Offset(cx + 90, cy + 60), Offset(cx + 170, cy + 90), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 170, cy + 90), Offset(cx + 155, cy + 90), 12f)
    // 上方腿（抬起）
    pThickLine(BODY, Offset(cx + 90, cy + 40), Offset(cx + 170, cy - 30 + legDy), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 170, cy - 30 + legDy), Offset(cx + 165, cy - 50 + legDy), 12f)

    // 主训肌群：臀中肌（髋外侧）
    pMuscle("臀中肌", Offset(cx + 70, cy + 70), Size(34, 24), MUSCLE, pulse)
}

// =====================================================
// 9. dead_bug  死虫式 —— 腹横肌 + 多裂肌
// =====================================================
private fun DrawScope.drawDeadBug(p: Float, pulse: Float, cx: Float, cy: Float) {
    val ext = (p - 0.5f) * 60f

    // 地面
    pLine(GROUND, Offset(20f, cy + 90), Offset(800f, cy + 90), strokeWidth = 3f)

    // 仰卧身体（顶视 / 正面）—— 头在顶
    pCircle(SKIN, 26f, Offset(cx, cy - 100), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 16, cy - 124), Size(32, 26), 12f)
    // 躯干
    pRoundRect(BODY, Offset(cx - 40, cy - 70), Size(80, 140), 18f)
    pRoundRect(BODY_LIGHT, Offset(cx - 40, cy - 70), Size(80, 30), 18f)

    // 双臂（向上）
    pThickLine(SKIN, Offset(cx - 25, cy - 30), Offset(cx - 80, cy - 70), 16f)
    pThickLine(SKIN, Offset(cx + 25, cy - 30), Offset(cx + 80, cy - 70), 16f)
    pCircle(SKIN, 10f, Offset(cx - 80, cy - 70), stroke = SKIN_DARK)
    pCircle(SKIN, 10f, Offset(cx + 80, cy - 70), stroke = SKIN_DARK)

    // 双腿
    pThickLine(BODY, Offset(cx - 18, cy + 70), Offset(cx - 18 + ext, cy + 130), 24f)
    pThickLine(SKIN_DARK, Offset(cx - 18 + ext, cy + 130), Offset(cx - 8 + ext, cy + 140), 12f)
    pThickLine(BODY, Offset(cx + 18, cy + 70), Offset(cx + 18 - ext, cy + 130), 24f)
    pThickLine(SKIN_DARK, Offset(cx + 18 - ext, cy + 130), Offset(cx + 8 - ext, cy + 140), 12f)

    // 主训肌群：腹横肌（核心）
    pMuscle("腹横肌", Offset(cx, cy + 5), Size(50, 50), MUSCLE, pulse)
    // 副训：多裂肌（腰后）
    pMuscle("多裂肌", Offset(cx, cy + 50), Size(36, 22), MUSCLE_2, pulse)
}

// =====================================================
// 10. bird_dog  鸟狗式 —— 腹横肌 + 多裂肌 + 臀大肌
// =====================================================
private fun DrawScope.drawBirdDog(p: Float, pulse: Float, cx: Float, cy: Float) {
    val ext = (p - 0.5f) * 60f

    // 地面
    pLine(GROUND, Offset(20f, cy + 100), Offset(800f, cy + 100), strokeWidth = 3f)

    // 四点支撑身体（侧视）—— 头在左
    pCircle(SKIN, 24f, Offset(cx - 130, cy + 50), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 144, cy + 28), Size(30, 24), 12f)
    // 躯干（水平）
    pRoundRect(BODY, Offset(cx - 100, cy + 30), Size(180, 50), 14f)
    pRoundRect(BODY_LIGHT, Offset(cx - 100, cy + 30), Size(180, 14), 14f)

    // 下方手（前臂撑地）
    pThickLine(BODY, Offset(cx - 130, cy + 60), Offset(cx - 145, cy + 95), 18f)
    pThickLine(SKIN_DARK, Offset(cx - 145, cy + 95), Offset(cx - 115, cy + 100), 14f)
    // 下方腿（跪地）
    pThickLine(BODY, Offset(cx + 80, cy + 50), Offset(cx + 130, cy + 95), 28f)
    pThickLine(SKIN_DARK, Offset(cx + 130, cy + 95), Offset(cx + 150, cy + 100), 14f)

    // 对侧手脚伸出
    pThickLine(ACCENT, Offset(cx - 130, cy + 50), Offset(cx - 200 - ext, cy - 30), 16f)  // 手臂
    pCircle(SKIN, 10f, Offset(cx - 200 - ext, cy - 30), stroke = SKIN_DARK)
    pThickLine(ACCENT, Offset(cx + 80, cy + 50), Offset(cx + 180 + ext, cy - 30), 18f)   // 腿
    pThickLine(SKIN_DARK, Offset(cx + 180 + ext, cy - 30), Offset(cx + 195 + ext, cy - 50), 12f)

    // 主训肌群：腹横肌（核心）
    pMuscle("腹横肌", Offset(cx - 20, cy + 10), Size(48, 26), MUSCLE, pulse)
    // 副训 1：多裂肌（腰后）
    pMuscle("多裂肌", Offset(cx + 20, cy + 50), Size(36, 18), MUSCLE_2, pulse)
    // 副训 2：臀大肌
    pMuscle("臀大肌", Offset(cx + 60, cy + 70), Size(34, 20), MUSCLE_2, pulse)
}

// =====================================================
// 11. 90_90_breath  90/90 呼吸 —— 腹横肌 + 膈肌
// =====================================================
private fun DrawScope.drawBreathing(p: Float, pulse: Float, cx: Float, cy: Float) {
    val breath = pulse * 30f

    // 地面
    pLine(GROUND, Offset(20f, cy + 100), Offset(800f, cy + 100), strokeWidth = 3f)

    // 仰卧身体（侧视）
    pCircle(SKIN, 26f, Offset(cx - 130, cy + 50), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 146, cy + 26), Size(32, 26), 12f)
    // 躯干（呼吸胀缩）
    pRoundRect(BODY, Offset(cx - 100, cy + 20), Size(180, 50 + breath), 16f)
    pRoundRect(BODY_LIGHT, Offset(cx - 100, cy + 20), Size(180, 16), 16f)
    // 髋
    pCircle(BODY, 20f, Offset(cx + 80, cy + 50), stroke = SKIN_DARK)
    // 弯腿（90/90）
    pThickLine(BODY, Offset(cx + 80, cy + 50), Offset(cx + 140, cy + 30), 24f)
    pThickLine(BODY, Offset(cx + 140, cy + 30), Offset(cx + 150, cy + 95), 24f)
    pThickLine(SKIN_DARK, Offset(cx + 150, cy + 95), Offset(cx + 130, cy + 100), 12f)
    // 手臂
    pThickLine(SKIN, Offset(cx - 100, cy + 40), Offset(cx - 50, cy - 20), 16f)
    pThickLine(SKIN, Offset(cx - 50, cy - 20), Offset(cx + 30, cy - 20), 16f)
    pCircle(SKIN, 10f, Offset(cx + 30, cy - 20), stroke = SKIN_DARK)

    // 主训肌群：膈肌（肋下）
    pMuscle("膈肌", Offset(cx - 30, cy + 5), Size(60, 22), MUSCLE, pulse)
    // 副训：腹横肌
    pMuscle("腹横肌", Offset(cx + 30, cy + 40), Size(40, 28), MUSCLE_2, pulse)
}

// =====================================================
// 12. terminal_knee_extension  TKE —— 股内侧斜肌 (VMO)
// =====================================================
private fun DrawScope.drawTke(p: Float, pulse: Float, cx: Float, cy: Float) {
    val kneeDy = (p - 0.5f) * 30f

    // 地面
    pLine(GROUND, Offset(20f, cy + 130), Offset(800f, cy + 130), strokeWidth = 3f)

    // 弹力带（左）
    pLine(MUSCLE_2, Offset(cx - 80, cy + 60), Offset(cx + 20, cy + 80), strokeWidth = 5f)

    // 头 + 躯干（侧视，右腿）
    pCircle(SKIN, 28f, Offset(cx - 30, cy - 100), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 46, cy - 124), Size(34, 26), 12f)
    pRoundRect(BODY, Offset(cx - 45, cy - 70), Size(40, 160), 12f)
    pRoundRect(BODY, Offset(cx - 50, cy + 70), Size(50, 30), 12f)
    // 后腿
    pThickLine(BODY, Offset(cx - 25, cy + 100), Offset(cx - 25, cy + 180), 30f)
    pThickLine(SKIN_DARK, Offset(cx - 40, cy + 180), Offset(cx - 5, cy + 180), 18f)
    // 前腿（弯曲+伸展）
    pThickLine(BODY, Offset(cx + 10, cy + 100), Offset(cx + 20, cy + 180 + kneeDy), 30f)
    pThickLine(SKIN_DARK, Offset(cx + 5, cy + 180 + kneeDy), Offset(cx + 40, cy + 180 + kneeDy), 18f)
    // 膝关节
    pCircle(SKIN, 14f, Offset(cx + 20, cy + 180 + kneeDy), stroke = SKIN_DARK)

    // 主训肌群：VMO（膝盖内上）
    val radius = 12f + pulse * 4f
    pCircle(MUSCLE, radius, Offset(cx + 5, cy + 165 + kneeDy))
    pLabel("股内侧斜肌", Offset(cx + 30, cy + 130 + kneeDy), MUSCLE, anchor = TextAnchor.LEFT)

    // 箭头
    val arrA = (sin(p * Math.PI.toFloat()) * 0.5f + 0.5f).coerceIn(0f, 1f)
    pArrow(Offset(cx - 20, cy + 180 + kneeDy), Offset(cx + 30, cy + 180 + kneeDy), ACCENT.copy(alpha = arrA))
}

// =====================================================
// 13. short_foot  短足训练 —— 足底内在肌 + 胫骨后肌
// =====================================================
private fun DrawScope.drawShortFoot(p: Float, pulse: Float, cx: Float, cy: Float) {
    val archH = 18f + pulse * 14f

    // 地面
    pLine(GROUND, Offset(20f, cy + 80), Offset(800f, cy + 80), strokeWidth = 3f)

    // 小腿（下半截，侧视）
    pRoundRect(BODY, Offset(cx - 30, cy - 60), Size(60, 100), 16f)
    pRoundRect(BODY_LIGHT, Offset(cx - 30, cy - 60), Size(60, 20), 16f)
    // 脚踝
    pCircle(SKIN, 18f, Offset(cx, cy + 50), stroke = SKIN_DARK)
    // 脚
    pOval(SKIN, Offset(cx - 80, cy + 50), Size(160, 36), stroke = SKIN_DARK)
    // 脚趾
    pCircle(SKIN, 6f, Offset(cx + 80, cy + 60), stroke = SKIN_DARK)

    // 主训肌群：足底内在肌（足弓）
    pArc(MUSCLE, Offset(cx - 50, cy + 30), Size(100, archH), 0f, 180f, strokeWidth = 8f)
    pLabel("足底内在肌", Offset(cx, cy + 90), MUSCLE, anchor = TextAnchor.CENTER)

    // 副训：胫骨后肌（小腿深部）
    pCircle(MUSCLE_2, 14f + pulse * 4f, Offset(cx + 12, cy + 0))
    pLabel("胫骨后肌", Offset(cx + 50, cy - 20), MUSCLE_2, anchor = TextAnchor.LEFT)

    // 箭头（足弓上提）
    val arrA = (sin(p * Math.PI.toFloat()) * 0.5f + 0.5f).coerceIn(0f, 1f)
    pArrow(Offset(cx, cy + 50), Offset(cx, cy - 20), ACCENT.copy(alpha = arrA))
}

// =====================================================
// 14. glute_bridge_curl  臀桥+球屈 —— 腘绳肌 + 臀大肌
// =====================================================
private fun DrawScope.drawBridgeCurl(p: Float, pulse: Float, cx: Float, cy: Float) {
    val arch = -pulse * 18f
    val ballX = cx + 130 + p * 50f
    val ballY = cy + 95 - (1f - kotlin.math.abs(p - 0.5f) * 2f) * 10f

    // 地面
    pLine(GROUND, Offset(20f, cy + 110), Offset(800f, cy + 110), strokeWidth = 3f)

    // 头 + 肩
    pCircle(SKIN, 26f, Offset(cx - 140, cy + 60), stroke = SKIN_DARK)
    pPath(SKIN_DARK, Offset(cx - 156, cy + 36), Size(34, 26), 13f)
    pRoundRect(BODY, Offset(cx - 110, cy + 50), Size(40, 30), 10f)

    // 躯干
    pArc(BODY, Offset(cx - 70, cy - 20 + arch), Size(180, 100), 180f, 180f, strokeWidth = 36f)

    // 髋
    pCircle(BODY, 22f, Offset(cx + 70, cy + 20 + arch), stroke = SKIN_DARK)

    // 弯腿
    pThickLine(SKIN_DARK, Offset(cx + 70, cy + 40 + arch), Offset(cx + 130, cy + 30), 22f)
    pThickLine(SKIN_DARK, Offset(cx + 130, cy + 30), Offset(ballX, ballY), 18f)

    // 球
    pCircle(ACCENT, 22f, Offset(ballX, ballY), stroke = SKIN_DARK)

    // 主训肌群：腘绳肌（大腿后）
    pMuscle("腘绳肌", Offset(cx + 90, cy + 30 + arch / 2), Size(40, 20), MUSCLE, pulse)
    // 副训：臀大肌
    pMuscle("臀大肌", Offset(cx + 50, cy + 10 + arch), Size(40, 28), MUSCLE_2, pulse)
}

// =====================================================
// 默认：通用人形剪影（仅作兜底）
// =====================================================
private fun DrawScope.drawDefault(p: Float, pulse: Float, cx: Float, cy: Float) {
    val sway = sin(p * Math.PI.toFloat()) * 6f
    pCircle(SKIN, 28f, Offset(cx + sway, cy - 80), stroke = SKIN_DARK)
    pRoundRect(BODY, Offset(cx - 30, cy - 45), Size(60, 110), 16f)
    pThickLine(SKIN, Offset(cx - 30, cy - 30), Offset(cx - 60 + sway, cy + 30), 18f)
    pThickLine(SKIN, Offset(cx + 30, cy - 30), Offset(cx + 60 - sway, cy + 30), 18f)
    pThickLine(BODY, Offset(cx - 15, cy + 65), Offset(cx - 20, cy + 130), 24f)
    pThickLine(BODY, Offset(cx + 15, cy + 65), Offset(cx + 20, cy + 130), 24f)
}

// =====================================================
// 绘图原语
// =====================================================
private fun DrawScope.pCircle(
    color: Color, radius: Float, center: Offset,
    style: Stroke = Stroke(), stroke: Color? = null
) {
    if (stroke != null) {
        drawCircle(color = stroke, radius = radius, center = center, style = Stroke(width = 3f))
    }
    drawCircle(color = color, radius = radius, center = center, style = style)
}

private fun DrawScope.pRect(color: Color, topLeft: Offset, size: Size) {
    drawRect(color = color, topLeft = topLeft, size = size)
}

private fun DrawScope.pRoundRect(color: Color, topLeft: Offset, size: Size, cornerRadius: Float) {
    drawRoundRect(
        color = color,
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
}

private fun DrawScope.pOval(
    color: Color, topLeft: Offset, size: Size, stroke: Color? = null
) {
    if (stroke != null) {
        drawOval(color = stroke, topLeft = topLeft, size = size, style = Stroke(width = 3f))
    }
    drawOval(color = color, topLeft = topLeft, size = size)
}

private fun DrawScope.pLine(color: Color, start: Offset, end: Offset, strokeWidth: Float = 3f) {
    drawLine(
        color = color, start = start, end = end,
        strokeWidth = strokeWidth,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

/** 粗线（四肢用）—— 圆头，两侧各留半宽让关节自然 */
private fun DrawScope.pThickLine(color: Color, from: Offset, to: Offset, width: Float) {
    drawLine(
        color = color, start = from, end = to,
        strokeWidth = width,
        cap = androidx.compose.ui.graphics.StrokeCap.Round
    )
}

private fun DrawScope.pArc(
    color: Color, topLeft: Offset, size: Size,
    startAngle: Float, sweepAngle: Float, strokeWidth: Float = 3f
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
        val headLen = 14f
        val head1 = Offset(to.x - ux * headLen - uy * 8f, to.y - uy * headLen + ux * 8f)
        val head2 = Offset(to.x - ux * headLen + uy * 8f, to.y - uy * headLen - ux * 8f)
        pLine(color, to, head1, strokeWidth = 3f)
        pLine(color, to, head2, strokeWidth = 3f)
    }
}

/** 简单椭圆路径（用来画头发/衣领） */
private fun DrawScope.pPath(color: Color, topLeft: Offset, size: Size, cornerRadius: Float) {
    drawRoundRect(
        color = color,
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
}

// =====================================================
// 肌肉高亮 + 文字标签
// =====================================================
private enum class TextAnchor { LEFT, CENTER, RIGHT }

/** 脉动椭圆 + 文字标签 */
private fun DrawScope.pMuscle(
    label: String, center: Offset, size: Size,
    color: Color, pulse: Float
) {
    val alpha = (sin(pulse * Math.PI.toFloat()) * 0.35f + 0.65f).coerceIn(0f, 1f)
    // 外光晕
    pOval(
        color.copy(alpha = alpha * 0.25f),
        Offset(center.x - size.width * 0.7f, center.y - size.height * 0.7f),
        Size(size.width * 1.4f, size.height * 1.4f)
    )
    // 主体
    pOval(
        color.copy(alpha = alpha),
        Offset(center.x - size.width / 2, center.y - size.height / 2),
        size
    )
    // 标签
    pLabel(label, Offset(center.x, center.y + size.height / 2 + 22), color, anchor = TextAnchor.CENTER)
}

private fun DrawScope.pLabel(
    text: String, position: Offset, color: Color,
    anchor: TextAnchor = TextAnchor.CENTER,
    textSize: Float = 26f
) {
    drawIntoCanvas { canvas ->
        val align = when (anchor) {
            TextAnchor.LEFT -> android.graphics.Paint.Align.LEFT
            TextAnchor.CENTER -> android.graphics.Paint.Align.CENTER
            TextAnchor.RIGHT -> android.graphics.Paint.Align.RIGHT
        }
        // 先画白色描边（让文字在任何背景上都清晰）
        val strokePaint = android.graphics.Paint().apply {
            isAntiAlias = true
            this.color = android.graphics.Color.WHITE
            this.textSize = textSize
            this.textAlign = align
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.nativeCanvas.drawText(text, position.x, position.y, strokePaint)
        // 再画彩色填充
        val fillPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            this.color = color.toArgb()
            this.textSize = textSize
            this.textAlign = align
            style = android.graphics.Paint.Style.FILL
        }
        canvas.nativeCanvas.drawText(text, position.x, position.y, fillPaint)
    }
}
