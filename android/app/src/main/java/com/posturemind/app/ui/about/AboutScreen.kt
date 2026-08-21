package com.posturemind.app.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.posturemind.app.data.AuthRepository
import com.posturemind.app.ui.theme.CompensatingMuscleBg
import com.posturemind.app.ui.theme.CompensatingMuscleFg
import com.posturemind.app.ui.theme.Primary
import com.posturemind.app.ui.theme.PrimaryDark
import com.posturemind.app.ui.theme.RootMuscleBg
import com.posturemind.app.ui.theme.RootMuscleFg
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val auth = androidx.compose.runtime.remember { AuthRepository(context) }
    val phone by auth.phoneFlow.collectAsState(initial = null)
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
                text = "核心理念",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.size(48.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                "体态问题的真相",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.size(12.dp))
            Text(
                "几乎所有的体态问题，都遵循一个规律：",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )
            Text(
                "不是\"哪块肌肉太紧\"，而是\"哪块肌肉该发力却没在发力\"。",
                color = PrimaryDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.size(20.dp))

            // 案例 1
            ConceptBlock(
                title = "🔍 案例：头前伸",
                description = "你觉得是\"上斜方肌太紧了，所以耸肩\"。但其实——",
                compensatingLabel = "代偿肌（看起来紧的）",
                compensatingList = listOf("上斜方肌", "肩胛提肌"),
                rootLabel = "该发力却没在发力的",
                rootList = listOf("颈深屈肌", "下斜方肌", "前锯肌"),
                insight = "真正的解决方案不是按摩放松上斜方肌，而是激活颈深屈肌和下斜方肌。当你把头收回正确位置时，上斜方肌自然就不需要那么紧了。"
            )

            Spacer(Modifier.size(16.dp))

            // 案例 2
            ConceptBlock(
                title = "🔍 案例：骨盆前倾",
                description = "你觉得是\"髂腰肌太紧，所以骨盆被拉向前\"。但其实——",
                compensatingLabel = "代偿肌（看起来紧的）",
                compensatingList = listOf("髂腰肌", "竖脊肌"),
                rootLabel = "该发力却没在发力的",
                rootList = listOf("臀大肌", "腹横肌", "腘绳肌"),
                insight = "拉伸髂腰肌只能暂时缓解。真正的解决是唤醒臀大肌，让骨盆有向后倾的原动力。"
            )

            Spacer(Modifier.size(16.dp))

            // 治疗逻辑
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "🎯 治疗逻辑",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.size(8.dp))
                    LogicStep(1, "识别", "从照片/视频看出你的体态偏差")
                    LogicStep(2, "归因", "定位是哪个\"该发力的肌肉\"在偷懒")
                    LogicStep(3, "激活", "通过针对性训练唤醒它（神经肌肉再教育）")
                    LogicStep(4, "整合", "在日常动作中让新模式成为习惯")
                }
            }

            Spacer(Modifier.size(24.dp))

            Text(
                "这不是\"治症状\"，而是\"治根源\"。\n每一块紧张的肌肉背后，都有一块沉睡的肌肉。",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 24.sp
            )

            Spacer(Modifier.size(40.dp))

            // 账号信息 + 退出
            if (!phone.isNullOrEmpty()) {
                Text(
                    "当前账号：$phone",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.size(12.dp))
            }
            val scope = rememberCoroutineScope()
            OutlinedButton(
                onClick = {
                    scope.launch {
                        auth.logout()
                        onLogout()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("退出登录", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun ConceptBlock(
    title: String,
    description: String,
    compensatingLabel: String,
    compensatingList: List<String>,
    rootLabel: String,
    rootList: List<String>,
    insight: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.size(8.dp))
            Text(
                description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Spacer(Modifier.size(12.dp))

            ComparisonRow(compensatingLabel, compensatingList, CompensatingMuscleBg, CompensatingMuscleFg)
            Spacer(Modifier.size(6.dp))
            ComparisonRow(rootLabel, rootList, RootMuscleBg, RootMuscleFg)

            Spacer(Modifier.size(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFDBEAFE))
                    .padding(12.dp)
            ) {
                Text(
                    "💡 $insight",
                    fontSize = 13.sp,
                    color = Color(0xFF1E3A8A),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    muscles: List<String>,
    bgColor: Color,
    fgColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(10.dp)
    ) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = fgColor
        )
        Spacer(Modifier.size(2.dp))
        Text(
            muscles.joinToString(" · "),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = fgColor
        )
    }
}

@Composable
private fun LogicStep(num: Int, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$num",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.size(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
