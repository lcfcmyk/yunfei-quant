package com.yunfei.quant.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.store by preferencesDataStore("yunfei_prefs")

class UserPrefs(private val context: Context) {
    companion object {
        val TOKEN = stringPreferencesKey("token")
        val NICK = stringPreferencesKey("nickname")
        val BASE_URL = stringPreferencesKey("base_url")
        val API_BOUND = booleanPreferencesKey("api_bound")
        val TUTORIAL_SEEN = booleanPreferencesKey("tutorial_seen")
        val ROM_GUIDE_SEEN = booleanPreferencesKey("rom_guide_seen")
        val PIN_HASH = stringPreferencesKey("pin_hash")
    }

    suspend fun saveToken(t: String) = context.store.edit { it[TOKEN] = t }
    suspend fun token(): String? = context.store.data.first()[TOKEN]

    suspend fun saveNickname(n: String) = context.store.edit { it[NICK] = n }
    suspend fun nickname(): String = context.store.data.first()[NICK] ?: "云飞"

    suspend fun baseUrl(): String =
        context.store.data.first()[BASE_URL] ?: "http://206.187.214.176:8080"

    suspend fun setBaseUrl(u: String) = context.store.edit { it[BASE_URL] = u }

    suspend fun apiBound(): Boolean = context.store.data.first()[API_BOUND] ?: false
    suspend fun setApiBound(v: Boolean) = context.store.edit { it[API_BOUND] = v }

    suspend fun tutorialSeen(): Boolean = context.store.data.first()[TUTORIAL_SEEN] ?: false
    suspend fun setTutorialSeen(v: Boolean) = context.store.edit { it[TUTORIAL_SEEN] = v }

    suspend fun romGuideSeen(): Boolean = context.store.data.first()[ROM_GUIDE_SEEN] ?: false
    suspend fun setRomGuideSeen(v: Boolean) = context.store.edit { it[ROM_GUIDE_SEEN] = v }

    suspend fun savePin(hash: String) = context.store.edit { it[PIN_HASH] = hash }
    suspend fun pinHash(): String? = context.store.data.first()[PIN_HASH]

    suspend fun clearAll() = context.store.edit { it.clear() }
}
