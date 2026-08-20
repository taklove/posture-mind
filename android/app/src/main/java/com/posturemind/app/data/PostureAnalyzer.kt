package com.posturemind.app.data

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * 体态分析器
 *
 * 输入：MediaPipe Pose 输出的 33 个关键点
 * 输出：检测到的体态问题列表
 */
class PostureAnalyzer {

    data class Point(val x: Float, val y: Float, val z: Float = 0f, val visibility: Float = 1f)

    /**
     * 主分析入口
     */
    fun analyze(landmarks: List<Point>, view: String): AnalysisResult {
        if (landmarks.size < 33) {
            return AnalysisResult(emptyList(), "未检测到人体关键点，请确保全身在画面内")
        }

        val issues = mutableListOf<PostureIssue>()

        // 关键点引用
        val leftEar = landmarks[7]
        val rightEar = landmarks[8]
        val leftShoulder = landmarks[11]
        val rightShoulder = landmarks[12]
        val leftHip = landmarks[23]
        val rightHip = landmarks[24]
        val leftKnee = landmarks[25]
        val rightKnee = landmarks[26]
        val leftAnkle = landmarks[27]
        val rightAnkle = landmarks[28]

        // 归一化参考：躯干长度
        val shoulderMid = mid(leftShoulder, rightShoulder)
        val hipMid = mid(leftHip, rightHip)
        val torsoLen = max(dist(shoulderMid, hipMid), 0.01f)
        val hipWidth = max(dist(leftHip, rightHip), 0.01f)

        // ============================================================
        // 1. 高低肩（前面/背面）
        // ============================================================
        if (view == "front" || view == "back") {
            val shoulderHeightDiff = abs(leftShoulder.y - rightShoulder.y) / torsoLen
            val hipHeightDiff = abs(leftHip.y - rightHip.y) / torsoLen

            if (shoulderHeightDiff > 0.05f) {
                val severity = if (shoulderHeightDiff > 0.10f) Severity.OBVIOUS else Severity.MILD
                issues.add(
                    PostureIssue(
                        pattern = Knowledge.SHOULDER_ASYMMETRY,
                        severity = severity,
                        score = shoulderHeightDiff.toDouble(),
                        measurements = mapOf("shoulderHeightDiff" to "%.3f".format(shoulderHeightDiff))
                    )
                )
            }
        }

        // ============================================================
        // 2. 头前伸 + 圆肩（侧面）
        // ============================================================
        if (view == "side") {
            val earMid = mid(leftEar, rightEar)
            val fhpOffset = abs(earMid.x - shoulderMid.x) / torsoLen
            val shoulderForward = abs(shoulderMid.x - hipMid.x) / torsoLen

            if (fhpOffset > 0.08f || shoulderForward > 0.10f) {
                val severity = if (fhpOffset > 0.15f || shoulderForward > 0.18f) Severity.OBVIOUS else Severity.MILD
                issues.add(
                    PostureIssue(
                        pattern = Knowledge.FHP_ROUNDED_SHOULDER,
                        severity = severity,
                        score = max(fhpOffset, shoulderForward).toDouble(),
                        measurements = mapOf(
                            "fhpOffset" to "%.3f".format(fhpOffset),
                            "shoulderForward" to "%.3f".format(shoulderForward)
                        )
                    )
                )
            }
        }

        // ============================================================
        // 3. 骨盆前倾/后倾（侧面）
        // ============================================================
        if (view == "side") {
            val kneeMid = mid(leftKnee, rightKnee)
            val hipKneeVec = vec(hipMid, kneeMid)
            val vertical = Point(0f, 1f)
            val pelvicAngle = angleDeg(hipKneeVec, vertical)

            if (pelvicAngle < 70) {
                val severity = if (pelvicAngle < 60) Severity.OBVIOUS else Severity.MILD
                issues.add(
                    PostureIssue(
                        pattern = Knowledge.ANTERIOR_PELVIC_TILT,
                        severity = severity,
                        score = (80 - pelvicAngle).toDouble(),
                        measurements = mapOf("pelvicAngle" to "%.1f".format(pelvicAngle))
                    )
                )
            } else {
                val shoulderVec = vec(shoulderMid, hipMid)
                val lowerBackAngle = angleDeg(shoulderVec, hipKneeVec)
                if (lowerBackAngle > 200) {
                    val severity = if (lowerBackAngle > 215) Severity.OBVIOUS else Severity.MILD
                    issues.add(
                        PostureIssue(
                            pattern = Knowledge.POSTERIOR_PELVIC_TILT,
                            severity = severity,
                            score = (lowerBackAngle - 200).toDouble(),
                            measurements = mapOf("lowerBackAngle" to "%.1f".format(lowerBackAngle))
                        )
                    )
                }
            }
        }

        // ============================================================
        // 4. 膝内扣（正面/背面）
        // ============================================================
        if (view == "front" || view == "back") {
            val kneeMid = mid(leftKnee, rightKnee)
            val ankleMid = mid(leftAnkle, rightAnkle)
            val kneeWidth = dist(leftKnee, rightKnee)
            val kneeHipRatio = kneeWidth / hipWidth
            val kneeOffset = dist(kneeMid, ankleMid) / dist(hipMid, ankleMid)

            if (kneeHipRatio < 0.65f && kneeOffset > 0.05f) {
                val severity = if (kneeHipRatio < 0.50f) Severity.OBVIOUS else Severity.MILD
                issues.add(
                    PostureIssue(
                        pattern = Knowledge.KNEE_VALGUS,
                        severity = severity,
                        score = (0.65f - kneeHipRatio).toDouble(),
                        measurements = mapOf(
                            "kneeHipRatio" to "%.2f".format(kneeHipRatio),
                            "kneeOffset" to "%.2f".format(kneeOffset)
                        )
                    )
                )
            }
        }

        // ============================================================
        // 5. 膝过伸（侧面）
        // ============================================================
        if (view == "side") {
            val kneeMid = mid(leftKnee, rightKnee)
            val ankleMid = mid(leftAnkle, rightAnkle)
            val hipKneeVec = vec(hipMid, kneeMid)
            val kneeAnkleVec = vec(kneeMid, ankleMid)
            val kneeAngle = angleDeg(hipKneeVec, kneeAnkleVec)

            if (kneeAngle > 185) {
                val severity = if (kneeAngle > 195) Severity.OBVIOUS else Severity.MILD
                issues.add(
                    PostureIssue(
                        pattern = Knowledge.KNEE_HYPEREXTENSION,
                        severity = severity,
                        score = (kneeAngle - 180).toDouble(),
                        measurements = mapOf("kneeAngle" to "%.1f".format(kneeAngle))
                    )
                )
            }
        }

        // ============================================================
        // 6. 头部侧倾（正面/背面）
        // ============================================================
        if (view == "front" || view == "back") {
            val earHeightDiff = abs(leftEar.y - rightEar.y) / torsoLen
            val earVec = vec(leftEar, rightEar)
            val horizontal = Point(1f, 0f)
            val headTiltAngle = angleDeg(earVec, horizontal)

            if (earHeightDiff > 0.04f || headTiltAngle > 5) {
                val severity = if (earHeightDiff > 0.08f || headTiltAngle > 10) Severity.OBVIOUS else Severity.MILD
                issues.add(
                    PostureIssue(
                        pattern = Knowledge.HEAD_LATERAL_TILT,
                        severity = severity,
                        score = max(earHeightDiff * 100, headTiltAngle).toDouble(),
                        measurements = mapOf(
                            "earHeightDiff" to "%.3f".format(earHeightDiff),
                            "headTiltAngle" to "%.1f".format(headTiltAngle)
                        )
                    )
                )
            }
        }

        // ============================================================
        // 7. 翼状肩胛（背面）
        // ============================================================
        if (view == "back") {
            val shoulderAsym = abs(leftShoulder.z - rightShoulder.z)
            if (shoulderAsym > 0.05f) {
                issues.add(
                    PostureIssue(
                        pattern = Knowledge.WINGED_SCAPULA,
                        severity = Severity.CHECK,
                        score = shoulderAsym.toDouble(),
                        measurements = mapOf("shoulderDepthDiff" to "%.3f".format(shoulderAsym)),
                        note = "建议做\"推墙测试\"确认：双手推墙时肩胛骨是否突出"
                    )
                )
            }
        }

        // 按严重度排序
        val severityRank = mapOf(Severity.OBVIOUS to 3, Severity.MILD to 2, Severity.CHECK to 1)
        issues.sortByDescending { severityRank[it.severity] ?: 0 }

        val summary = if (issues.isEmpty()) {
            "✓ 体态基本良好！继续保持规律训练。"
        } else {
            "检测到 ${issues.size} 项体态问题，建议针对训练。"
        }

        return AnalysisResult(issues, summary)
    }

    // ============================================================
    // 几何工具
    // ============================================================
    private fun dist(a: Point, b: Point) = hypot(a.x - b.x, a.y - b.y)

    private fun mid(a: Point, b: Point) = Point((a.x + b.x) / 2, (a.y + b.y) / 2)

    private fun vec(from: Point, to: Point) = Point(to.x - from.x, to.y - from.y)

    private fun angleDeg(v1: Point, v2: Point): Float {
        val dot = v1.x * v2.x + v1.y * v2.y
        val m1 = hypot(v1.x, v1.y)
        val m2 = hypot(v2.x, v2.y)
        if (m1 == 0f || m2 == 0f) return 0f
        val cos = max(-1f, min(1f, dot / (m1 * m2)))
        return (acos(cos) * 180f / Math.PI).toFloat()
    }
}

data class AnalysisResult(
    val issues: List<PostureIssue>,
    val summary: String
)
