package com.posturemind.app.ui.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.posturemind.app.data.Knowledge
import com.posturemind.app.ui.exercise.svg.ExerciseSvg
import com.posturemind.app.ui.theme.Accent
import com.posturemind.app.ui.theme.Border
import com.posturemind.app.ui.theme.Danger
import com.posturemind.app.ui.theme.Primary
import com.posturemind.app.ui.theme.Success
import com.posturemind.app.viewmodel.PostureViewModel
import kotlinx.coroutines.delay

@Composable
fun ExerciseScreen(
    exerciseId: String,
    viewModel: PostureViewModel,
    onBack: () -> Unit
) {
    val exercise = remember(exerciseId) { Knowledge.findExercise(exerciseId) }
    val completed by viewModel.completedToday.collectAsState()
    val isDone = exercise != null && exercise.id in completed

    var timerSeconds by remember { mutableStateOf(30) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }
        if (timerSeconds == 0) isRunning = false
    }

    if (exercise == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("未找到该训练动作")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
            }
            Text(
                text = exercise.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(48.dp))
        }

        // Hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Primary)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    exercise.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    "针对：${exercise.target}",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // SVG 插画
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ExerciseSvg(exercise.svgKey)
            }
        }

        // 处方
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Accent.copy(alpha = 0.2f))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PrescriptionItem(value = "${exercise.sets}", label = "组数")
            PrescriptionItem(
                value = exercise.reps.replace(Regex("[^0-9]"), "").ifEmpty { "—" },
                label = if (exercise.reps.contains("次")) "次" else "次/秒"
            )
            PrescriptionItem(value = "L${exercise.level}", label = "难度")
        }

        // 要点
        SectionHeader("✓ 动作要点", Success)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            exercise.cues.forEach { cue ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✓ ", color = Success, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(cue, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
        }

        // 避免
        if (exercise.avoid.isNotEmpty()) {
            SectionHeader("✗ 避免错误", Danger)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                exercise.avoid.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✗ ", color = Danger, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item, fontSize = 14.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        // 计时器
        SectionHeader("⏱ 计时器", Primary)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val mins = timerSeconds / 60
                val secs = timerSeconds % 60
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )
                Spacer(Modifier.size(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTimerButton("+10s") { timerSeconds += 10 }
                    PrimaryTimerButton(if (isRunning) "暂停" else "开始") {
                        isRunning = !isRunning
                    }
                    OutlinedTimerButton("重置") {
                        isRunning = false
                        timerSeconds = 30
                    }
                }
            }
        }

        Spacer(Modifier.size(20.dp))

        // 标记完成
        Button(
            onClick = { viewModel.toggleExerciseDone(exercise.id) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDone) MaterialTheme.colorScheme.outline else Success
            )
        ) {
            Text(
                if (isDone) "✓ 已完成（再点取消）" else "标记完成 ✓",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.size(40.dp))
    }
}

@Composable
private fun SectionHeader(text: String, accent: Color) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp),
        color = accent
    )
}

@Composable
private fun PrescriptionItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Primary
        )
        Spacer(Modifier.size(2.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = Color(0xFF92400E),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PrimaryTimerButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
private fun OutlinedTimerButton(text: String, onClick: () -> Unit) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text)
    }
}
