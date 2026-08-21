package com.posturemind.app.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.posturemind.app.data.PostureIssue
import com.posturemind.app.data.Severity
import com.posturemind.app.ui.theme.Accent
import com.posturemind.app.ui.theme.Border
import com.posturemind.app.ui.theme.CompensatingMuscleBg
import com.posturemind.app.ui.theme.CompensatingMuscleFg
import com.posturemind.app.ui.theme.Danger
import com.posturemind.app.ui.theme.Primary
import com.posturemind.app.ui.theme.RootMuscleBg
import com.posturemind.app.ui.theme.RootMuscleFg
import com.posturemind.app.ui.theme.Success
import com.posturemind.app.viewmodel.PostureViewModel

@Composable
fun ResultScreen(
    viewModel: PostureViewModel,
    onStartTraining: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.assessment.collectAsState()
    val trainingState by viewModel.training.collectAsState()
    // IssueCard 需要 PostureIssue 完整信息（pattern.shortDesc / 代偿肌 / 该发力肌），
    // 这些只在 runFinalAnalysis 写入的 training.issues 里有
    val issues = trainingState.issues

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
                text = "诊断结果",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(48.dp))
        }

        // 总结
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (issues.isEmpty()) {
                Text("🎉 你的体态很棒", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "未检测到明显的体态偏差，继续保持！",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                val obvious = issues.count { it.severity == Severity.OBVIOUS }
                val mild = issues.count { it.severity == Severity.MILD }
                Text("📋 检测到 ${issues.size} 项", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                val text = buildString {
                    if (obvious > 0) append("其中 ")
                    if (obvious > 0) append("$obvious 项 需要重点关注")
                    if (obvious > 0 && mild > 0) append("，")
                    if (mild > 0) append("$mild 项轻度问题")
                }
                Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        // 问题列表
        if (issues.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("✨ 体态优秀", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "没发现需要优先解决的问题。建议每周 2-3 次核心 + 臀部训练做预防性强化。",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(issues) { issue -> IssueCard(issue) }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 行动按钮
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onStartTraining,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("开始针对性训练 →", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun IssueCard(issue: PostureIssue) {
    val borderColor = when (issue.severity) {
        Severity.OBVIOUS -> Danger
        Severity.MILD -> Accent
        Severity.CHECK -> Color(0xFF3B82F6)
    }
    val severityBg = when (issue.severity) {
        Severity.OBVIOUS -> Color(0xFFFEE2E2)
        Severity.MILD -> Color(0xFFFEF3C7)
        Severity.CHECK -> Color(0xFFDBEAFE)
    }
    val severityFg = when (issue.severity) {
        Severity.OBVIOUS -> Danger
        Severity.MILD -> Color(0xFFB45309)
        Severity.CHECK -> Color(0xFF1E40AF)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 4.dp, color = borderColor, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(issue.pattern.icon, fontSize = 22.sp)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        issue.pattern.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(severityBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        issue.severity.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = severityFg
                    )
                }
            }

            Spacer(Modifier.size(8.dp))
            Text(
                issue.pattern.shortDesc,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(Modifier.size(10.dp))

            // 代偿肌
            MuscleSection(
                label = "🔴 代偿肌（看起来紧的 — 不是问题根源）",
                bgColor = CompensatingMuscleBg,
                fgColor = CompensatingMuscleFg,
                muscles = issue.pattern.compensatingMuscles.map { it.name }
            )

            Spacer(Modifier.size(8.dp))

            // 该发力肌
            MuscleSection(
                label = "🟢 该发力却没发力的（这才是根源）",
                bgColor = RootMuscleBg,
                fgColor = RootMuscleFg,
                muscles = issue.pattern.shouldBeStrong.map { it.name }
            )
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun MuscleSection(
    label: String,
    bgColor: Color,
    fgColor: Color,
    muscles: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(12.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fgColor)
        Spacer(Modifier.size(6.dp))
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            muscles.forEach { muscle ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(muscle, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
