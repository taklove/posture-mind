package com.posturemind.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val Context.authStore by preferencesDataStore(name = "auth")

/**
 * 鉴权仓库
 * - 调用 manus.xin 下的 /api/auth/ 端点拿 JWT
 * - 把 token / user_id / phone 存到独立 DataStore（跟历史数据分开）
 */
class AuthRepository(private val context: Context) {

    val tokenFlow: Flow<String?> = context.authStore.data.map { it[KEY_TOKEN] }
    val phoneFlow: Flow<String?> = context.authStore.data.map { it[KEY_PHONE] }
    val userIdFlow: Flow<Long?> = context.authStore.data.map { it[KEY_USER_ID] }

    suspend fun currentToken(): String? = context.authStore.data.first()[KEY_TOKEN]

    suspend fun sendCode(phone: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("phone", phone).toString()
            val resp = post("/api/auth/send-code", body, token = null)
            val json = JSONObject(resp)
            if (!json.optBoolean("ok", false)) {
                error(json.optString("message", "发送失败"))
            }
        }
    }

    suspend fun verify(phone: String, code: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val body = JSONObject().put("phone", phone).put("code", code).toString()
            val resp = post("/api/auth/verify", body, token = null)
            val json = JSONObject(resp)
            val token = json.optString("token")
            val userId = json.optLong("user_id")
            if (token.isEmpty()) error("返回数据异常")
            context.authStore.edit { prefs ->
                prefs[KEY_TOKEN] = token
                prefs[KEY_PHONE] = phone
                prefs[KEY_USER_ID] = userId
            }
        }
    }

    suspend fun logout() {
        context.authStore.edit { it.clear() }
    }

    /**
     * 通用 POST（带可选 Bearer token）
     */
    private fun post(path: String, body: String, token: String?): String {
        val url = URL(BASE_URL + path)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
            if (!token.isNullOrEmpty()) {
                setRequestProperty("Authorization", "Bearer $token")
            }
        }
        try {
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            if (code !in 200..299) {
                // 尝试解析 FastAPI 的 detail
                val msg = try { JSONObject(text).optString("detail", text) } catch (_: Exception) { text }
                error("$code $msg")
            }
            return text
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        const val BASE_URL = "https://manus.xin"
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_PHONE = stringPreferencesKey("phone")
        private val KEY_USER_ID = longPreferencesKey("user_id")
    }
}
