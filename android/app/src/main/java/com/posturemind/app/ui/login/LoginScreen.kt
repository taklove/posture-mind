package com.posturemind.app.ui.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.posturemind.app.data.AuthRepository
import com.posturemind.app.ui.theme.Primary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    auth: AuthRepository,
    onLoggedIn: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var codeSent by remember { mutableStateOf(false) }
    var cooldown by remember { mutableStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }

    // 倒计时
    LaunchedEffect(cooldown) {
        if (cooldown > 0) {
            kotlinx.coroutines.delay(1000)
            cooldown -= 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("正形", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Primary)
        Spacer(Modifier.height(4.dp))
        Text(
            "PostureMind",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(40.dp))
        Text(
            "📱 手机号登录",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { input ->
                // 只允许数字，最多 11 位
                if (input.length <= 11 && input.all { it.isDigit() }) {
                    phone = input
                    error = null
                }
            },
            label = { Text("手机号") },
            placeholder = { Text("11 位手机号") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = code,
                onValueChange = { input ->
                    if (input.length <= 6 && input.all { it.isDigit() }) {
                        code = input
                        error = null
                    }
                },
                label = { Text("验证码") },
                placeholder = { Text("6 位数字") },
                singleLine = true,
                enabled = codeSent,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.padding(horizontal = 4.dp))
            TextButton(
                onClick = {
                    if (phone.length != 11) {
                        error = "请输入完整的 11 位手机号"
                        return@TextButton
                    }
                    if (cooldown > 0 || busy) return@TextButton
                    busy = true
                    error = null
                    info = null
                    scope.launch {
                        val r = auth.sendCode(phone)
                        busy = false
                        r.onSuccess {
                            codeSent = true
                            info = "验证码已发送（mock 模式：到服务端日志查看，路径 /var/log/posturemind-api.log）"
                            cooldown = 60
                        }
                        r.onFailure { e ->
                            error = e.message ?: "发送失败"
                        }
                    }
                },
                enabled = phone.length == 11 && cooldown == 0 && !busy,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(if (cooldown > 0) "${cooldown}s 后重试" else if (codeSent) "重新发送" else "获取验证码")
            }
        }

        if (info != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                info!!,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                "⚠️ $error",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                if (phone.length != 11) {
                    error = "请输入手机号"
                    return@Button
                }
                if (code.length != 6) {
                    error = "请输入 6 位验证码"
                    return@Button
                }
                if (busy) return@Button
                busy = true
                error = null
                scope.launch {
                    val r = auth.verify(phone, code)
                    busy = false
                    r.onSuccess { onLoggedIn() }
                    r.onFailure { e -> error = e.message ?: "登录失败" }
                }
            },
            enabled = phone.length == 11 && code.length == 6 && !busy,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(
                if (busy) "登录中..." else "登 录",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "首次使用请先获取验证码，验证码 5 分钟内有效",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
