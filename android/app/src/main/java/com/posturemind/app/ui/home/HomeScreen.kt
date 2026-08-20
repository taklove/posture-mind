package com.posturemind.app.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.posturemind.app.ui.theme.Accent
import com.posturemind.app.ui.theme.AccentLight
import com.posturemind.app.ui.theme.Primary
import com.posturemind.app.ui.theme.PrimaryDark

@Composable
fun HomeScreen(
    onStartCapture: () -> Unit,
    onAbout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        AccentLight.copy(alpha = 0.3f),
                        Color.White,
                        Primary.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Hero
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🧘", fontSize = 32.sp)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = "正形",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = buildString {
                        append("找到那块\n")
                        append("该有力却无力\n")
                        append("的肌肉")
                    },
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 46.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "体态问题，不是\"哪里太紧\"\n而是哪里该发力却没发力",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onStartCapture,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "开始评估 →",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onAbout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("了解理念", color = Primary)
                }
            }

            // Feature grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                item {
                    FeatureCard(
                        icon = "📸",
                        title = "拍照即分析",
                        desc = "3 张照片（正/侧/背），AI 识别你的体态问题"
                    )
                }
                item {
                    FeatureCard(
                        icon = "🎯",
                        title = "精准诊断",
                        desc = "不是看哪块肌肉紧，而是看哪块该发力却没发"
                    )
                }
                item {
                    FeatureCard(
                        icon = "💪",
                        title = "针对训练",
                        desc = "每天 10 分钟，唤醒沉睡的肌肉"
                    )
                }
                item {
                    FeatureCard(
                        icon = "📈",
                        title = "持续跟踪",
                        desc = "每周重新评估，看见身体的变化"
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: String,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = icon, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
