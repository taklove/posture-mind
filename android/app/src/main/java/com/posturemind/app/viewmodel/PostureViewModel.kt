package com.posturemind.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posturemind.app.camera.PoseDetector
import com.posturemind.app.data.AssessmentResult
import com.posturemind.app.data.CaptureView
import com.posturemind.app.data.HistoryStore
import com.posturemind.app.data.IssueSummary
import com.posturemind.app.data.Knowledge
import com.posturemind.app.data.PostureAnalyzer
import com.posturemind.app.data.PostureIssue
import com.posturemind.app.data.Severity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/**
 * 全局状态管理
 */
class PostureViewModel(app: Application) : AndroidViewModel(app) {

    private val historyStore = HistoryStore(app)

    // ============================================================
    // 评估状态
    // ============================================================
    data class AssessmentState(
        val currentView: CaptureView = CaptureView.FRONT,
        val captured: Map<CaptureView, CapturedFrame> = emptyMap(),
        val analyzing: Boolean = false,
        val lastResult: AssessmentResult? = null
    )

    data class CapturedFrame(
        val bitmap: Bitmap,
        val landmarks: List<PostureAnalyzer.Point>,
        val analysis: List<PostureIssue>,
        val summary: String
    )

    private val _assessment = MutableStateFlow(AssessmentState())
    val assessment: StateFlow<AssessmentState> = _assessment.asStateFlow()

    // ============================================================
    // 训练状态
    // ============================================================
    private val _training = MutableStateFlow(TrainingState())
    val training: StateFlow<TrainingState> = _training.asStateFlow()

    data class TrainingState(
        val issues: List<PostureIssue> = emptyList(),
        val plan: List<com.posturemind.app.data.Exercise> = emptyList(),
        val completed: Map<String, Long> = emptyMap()
    )

    val history: StateFlow<List<AssessmentResult>> = historyStore.history
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val completedToday: StateFlow<Map<String, Long>> = historyStore.completedToday
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    // ============================================================
    // Pose Detector（懒初始化）
    // ============================================================
    private var poseDetector: PoseDetector? = null

    fun getOrCreatePoseDetector(onResult: (List<PostureAnalyzer.Point>?) -> Unit): PoseDetector {
        if (poseDetector == null) {
            poseDetector = PoseDetector(getApplication()) { result ->
                val points = poseDetector?.toAnalyzerPoints(result)
                onResult(points)
            }
            poseDetector?.setup()
        }
        return poseDetector!!
    }

    // ============================================================
    // 评估流程
    // ============================================================
    fun setCurrentView(view: CaptureView) {
        _assessment.value = _assessment.value.copy(currentView = view)
    }

    fun saveCapturedFrame(view: CaptureView, bitmap: Bitmap, landmarks: List<PostureAnalyzer.Point>) {
        val analyzer = PostureAnalyzer()
        val result = analyzer.analyze(landmarks, view.name.lowercase())
        val frame = CapturedFrame(bitmap, landmarks, result.issues, result.summary)
        _assessment.value = _assessment.value.copy(
            captured = _assessment.value.captured + (view to frame)
        )
    }

    /**
     * 拍完 3 个 view 后，分析合并结果
     */
    fun runFinalAnalysis() {
        val state = _assessment.value
        if (state.captured.isEmpty()) return

        _assessment.value = state.copy(analyzing = true)

        // 去重（按 pattern id 保留严重度最高的）
        val allIssues = state.captured.values.flatMap { it.analysis }
        val issueMap = mutableMapOf<String, PostureIssue>()
        val severityRank = mapOf(
            Severity.OBVIOUS to 3,
            Severity.MILD to 2,
            Severity.CHECK to 1
        )
        for (issue in allIssues) {
            val existing = issueMap[issue.pattern.id]
            if (existing == null || (severityRank[issue.severity] ?: 0) > (severityRank[existing.severity] ?: 0)) {
                issueMap[issue.pattern.id] = issue
            }
        }
        val finalIssues = issueMap.values.sortedByDescending {
            severityRank[it.severity] ?: 0
        }

        val summary = IssueSummary(
            id = "session_${System.currentTimeMillis()}",
            name = "本次评估",
            severity = if (finalIssues.any { it.severity == Severity.OBVIOUS }) Severity.OBVIOUS else Severity.MILD,
            icon = "📋"
        )

        val result = AssessmentResult(
            timestamp = System.currentTimeMillis(),
            issues = finalIssues.map {
                IssueSummary(
                    id = it.pattern.id,
                    name = it.pattern.name,
                    severity = it.severity,
                    icon = it.pattern.icon
                )
            }
        )

        // 保存到历史
        viewModelScope.launch {
            historyStore.saveAssessment(result)
        }

        // 生成训练计划
        val plan = if (finalIssues.isEmpty()) {
            Knowledge.getGenericPlan()
        } else {
            Knowledge.buildTrainingPlan(finalIssues)
        }

        _assessment.value = state.copy(analyzing = false, lastResult = result)
        _training.value = TrainingState(
            issues = finalIssues,
            plan = plan,
            completed = _training.value.completed
        )
    }

    fun toggleExerciseDone(id: String) {
        viewModelScope.launch {
            historyStore.toggleExerciseDone(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        poseDetector?.close()
    }
}
