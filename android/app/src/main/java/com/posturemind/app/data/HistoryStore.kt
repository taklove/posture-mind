package com.posturemind.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "posture_mind")

/**
 * 本地存储：评估历史 + 训练完成记录
 */
class HistoryStore(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    // ============================================================
    // 评估历史
    // ============================================================
    val history: Flow<List<AssessmentResult>> = context.dataStore.data.map { prefs ->
        val raw = prefs[KEY_HISTORY] ?: return@map emptyList()
        try {
            json.decodeFromString<List<AssessmentResult>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveAssessment(result: AssessmentResult) {
        context.dataStore.edit { prefs ->
            val raw = prefs[KEY_HISTORY] ?: "[]"
            val current = try {
                json.decodeFromString<List<AssessmentResult>>(raw)
            } catch (e: Exception) {
                emptyList()
            }
            val updated = (listOf(result) + current).take(20)
            prefs[KEY_HISTORY] = json.encodeToString(updated)
        }
    }

    // ============================================================
    // 训练完成记录（按天分）
    // ============================================================
    val completedToday: Flow<Map<String, Long>> = context.dataStore.data.map { prefs ->
        val todayKey = todayKey()
        val raw = prefs[completedKey(todayKey)] ?: return@map emptyMap()
        try {
            json.decodeFromString<Map<String, Long>>(raw)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun toggleExerciseDone(id: String) {
        context.dataStore.edit { prefs ->
            val todayKey = todayKey()
            val key = completedKey(todayKey)
            val raw = prefs[key] ?: "{}"
            val current = try {
                json.decodeFromString<Map<String, Long>>(raw)
            } catch (e: Exception) {
                emptyMap()
            }
            val updated = if (current.containsKey(id)) {
                current - id
            } else {
                current + (id to System.currentTimeMillis())
            }
            prefs[key] = json.encodeToString(updated)
        }
    }

    private fun todayKey(): String {
        val cal = java.util.Calendar.getInstance()
        return "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.MONTH) + 1}-${cal.get(java.util.Calendar.DAY_OF_MONTH)}"
    }

    private fun completedKey(day: String) = stringPreferencesKey("completed_$day")

    companion object {
        private val KEY_HISTORY = stringPreferencesKey("history")
    }
}
