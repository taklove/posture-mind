package com.posturemind.app.ui.training

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.posturemind.app.data.Exercise
import com.posturemind.app.ui.theme.Primary
import com.posturemind.app.ui.theme.PrimaryDark
import com.posturemind.app.ui.theme.Success
import com.posturemind.app.viewmodel.PostureViewModel

@Composable
fun TrainingScreen(
    viewModel: PostureViewModel,
    onExerciseClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val training by viewModel.training.collectAsState()
    val completed by viewModel.completedToday.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = "今日训练",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(48.dp))
        }

        // 训练计划总结
        val issueCount = training.issues.size
        val planSize = training.plan.size
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Primary)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = if (issueCount == 0) "🎉 没有需要优先训练的问题"
                    else "💪 针对你的 $issueCount 项体态问题",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = if (issueCount == 0) "建议每周保持 2-3 次核心和臀部训练"
                    else "${training.issues.joinToString("、") { it.pattern.name }}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    "每天 10 分钟 · 唤醒沉睡的肌肉",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // 进度条
        val doneCount = training.plan.count { it.id in completed }
        val progress = if (planSize > 0) doneCount.toFloat() / planSize else 0f

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Success,
                trackColor = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.size(4.dp))
            Text(
                "$doneCount / $planSize 完成",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.size(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(training.plan) { ex ->
                ExerciseRow(
                    exercise = ex,
                    done = ex.id in completed,
                    onClick = { onExerciseClick(ex.id) },
                    onCheck = { viewModel.toggleExerciseDone(ex.id) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseRow(
    exercise: Exercise,
    done: Boolean,
    onClick: () -> Unit,
    onCheck: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .let { if (done) it.border(2.dp, Success, RoundedCornerShape(16.dp)) else it },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (done) Success.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFEF3C7)),
                contentAlignment = Alignment.Center
            ) {
                Text(getEmoji(exercise.id), fontSize = 28.sp)
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (done) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    "针对：${exercise.target}",
                    fontSize = 11.sp,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.size(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "${exercise.sets} 组",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        exercise.reps,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (done) Success else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (done) Success else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable(onClick = onCheck),
                contentAlignment = Alignment.Center
            ) {
                if (done) Text("✓", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

private fun getEmoji(id: String) = when (id) {
    "chin_tuck" -> "😐"
    "supine_head_lift" -> "😴"
    "prone_y_raise" -> "✋"
    "wall_slide" -> "🧱"
    "scap_pushup" -> "🤲"
    "glute_bridge" -> "🌉"
    "single_leg_bridge" -> "🦵"
    "clamshell" -> "🐚"
    "side_leg_raise" -> "🦿"
    "dead_bug" -> "🪲"
    "bird_dog" -> "🐕"
    "breath_90_90" -> "🫁"
    "bridge_curl" -> "⚽"
    "tke" -> "🦵"
    "short_foot" -> "🦶"
    else -> "💪"
}
