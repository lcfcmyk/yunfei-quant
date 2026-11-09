package com.yunfei.quant.ui.vm

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunfei.quant.data.ApiKeyInfo
import com.yunfei.quant.data.ApiResult
import com.yunfei.quant.data.LoginReq
import com.yunfei.quant.data.Overview
import com.yunfei.quant.data.PlaceOrderReq
import com.yunfei.quant.data.RetrofitClient
import com.yunfei.quant.data.StrategyItem
import com.yunfei.quant.data.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val loading: Boolean = false,
    val overview: Overview? = null,
    val strategies: List<StrategyItem> = emptyList(),
    val logs: List<String> = emptyList(),
    val apiKey: ApiKeyInfo? = null,
    val error: String? = null,
)

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = UserPrefs(app)

    var loggedIn by mutableStateOf(false)
        private set
    var apiBound by mutableStateOf(false)
        private set
    var tutorialSeen by mutableStateOf(false)
        private set
    var nickname by mutableStateOf("云飞")
        private set
    var baseUrl by mutableStateOf("http://206.187.214.176:8080")
        private set

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        viewModelScope.launch {
            baseUrl = prefs.baseUrl()
            nickname = prefs.nickname()
            apiBound = prefs.apiBound()
            tutorialSeen = prefs.tutorialSeen()
            loggedIn = !prefs.token().isNullOrBlank()
        }
    }

    private fun service() = RetrofitClient.get(baseUrl)

    private suspend fun tokenHeader(): String = prefs.token() ?: ""

    // ---------- 登录 ----------
    fun login(account: String, password: String, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val r = service().login(LoginReq(account, password))
                if (r.ok && r.data != null) {
                    prefs.saveToken(r.data.token)
                    prefs.saveNickname(r.data.nickname)
                    prefs.setApiBound(r.data.apiBound)
                    prefs.setTutorialSeen(r.data.tutorialSeen)
                    loggedIn = true
                    apiBound = r.data.apiBound
                    tutorialSeen = r.data.tutorialSeen
                    nickname = r.data.nickname
                    onDone(true, null)
                } else {
                    onDone(false, r.message ?: "账号或密码错误")
                }
            } catch (e: Exception) {
                onDone(false, e.message ?: "网络异常，请检查服务器地址")
            } finally {
                _state.value = _state.value.copy(loading = false)
            }
        }
    }

    fun updateBaseUrl(url: String) {
        baseUrl = url.trimEnd('/')
        viewModelScope.launch { prefs.setBaseUrl(baseUrl) }
    }

    fun markApiBound() {
        apiBound = true
        viewModelScope.launch { prefs.setApiBound(true) }
    }

    fun markTutorialSeen() {
        tutorialSeen = true
        viewModelScope.launch { prefs.setTutorialSeen(true) }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.clearAll()
            loggedIn = false
            apiBound = false
            tutorialSeen = false
            _state.value = UiState()
        }
    }

    // ---------- 数据 ----------
    fun refreshAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                val t = tokenHeader()
                val ov = service().overview(t)
                val st = service().strategies(t)
                _state.value = _state.value.copy(
                    overview = ov.data,
                    strategies = st.data ?: ov.data?.strategies ?: emptyList(),
                    loading = false,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun loadLogs() {
        viewModelScope.launch {
            try {
                val r = service().logs(tokenHeader(), 100)
                _state.value = _state.value.copy(logs = r.data ?: emptyList())
            } catch (_: Exception) {
            }
        }
    }

    fun loadApiKey() {
        viewModelScope.launch {
            try {
                val r = service().apiKeyInfo(tokenHeader())
                _state.value = _state.value.copy(apiKey = r.data)
            } catch (_: Exception) {
            }
        }
    }

    fun toggleStrategy(id: String, running: Boolean) {
        viewModelScope.launch {
            try {
                service().toggleStrategy(tokenHeader(), id, running)
                refreshAll()
            } catch (_: Exception) {
            }
        }
    }

    fun pauseAll(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true)
            try {
                val r: ApiResult<Unit> = service().pauseAll(tokenHeader())
                refreshAll()
                onDone(r.ok)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false)
                onDone(false)
            }
        }
    }

    fun placeOrder(req: PlaceOrderReq, onDone: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val r = service().placeOrder(tokenHeader(), req)
                onDone(r.ok, r.message)
            } catch (e: Exception) {
                onDone(false, e.message)
            }
        }
    }
}
