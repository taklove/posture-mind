package com.posturemind.app.data

import kotlinx.serialization.Serializable

/**
 * 体态问题模式
 *
 * 哲学：
 * - 代偿肌：看起来紧张的，但其实是症状
 * - 应发力肌：应该工作但被抑制的，这才是根源
 */
@Serializable
data class PosturePattern(
    val id: String,
    val name: String,
    val nameEn: String,
    val shortDesc: String,
    val icon: String,
    val views: List<String>,
    val compensatingMuscles: List<Muscle>,
    val shouldBeStrong: List<Muscle>,
    val visualSignals: List<String>
)

@Serializable
data class Muscle(
    val name: String,
    val role: String? = null,
    val reason: String? = null
)

/**
 * 检测到的体态问题
 */
@Serializable
data class PostureIssue(
    val pattern: PosturePattern,
    val severity: Severity,
    val score: Double,
    val measurements: Map<String, String> = emptyMap(),
    val note: String? = null
)

@Serializable
enum class Severity(val label: String) {
    OBVIOUS("明显"),
    MILD("轻度"),
    CHECK("待确认")
}

/**
 * 训练动作
 */
@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val target: String,
    val level: Int,
    val sets: Int,
    val reps: String,
    val duration: String,
    val cues: List<String>,
    val avoid: List<String> = emptyList(),
    val svgKey: String
)

/**
 * 视图方向
 */
enum class CaptureView(val displayName: String) {
    FRONT("正面"),
    SIDE("侧面"),
    BACK("背面")
}

/**
 * 一次评估结果
 */
@Serializable
data class AssessmentResult(
    val timestamp: Long,
    val issues: List<IssueSummary>
)

@Serializable
data class IssueSummary(
    val id: String,
    val name: String,
    val severity: Severity,
    val icon: String
)
