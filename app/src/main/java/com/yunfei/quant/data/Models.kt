package com.yunfei.quant.data

data class LoginReq(val account: String, val password: String)

data class LoginResp(
    val token: String,
    val nickname: String,
    val apiBound: Boolean,
    val tutorialSeen: Boolean,
)

data class Overview(
    val equity: Double,
    val todayPnl: Double,
    val todayPnlPct: Double,
    val availableMargin: Double,
    val usedMargin: Double,
    val marginRatio: Double,
    val liqPrice: Double?,
    val positionsCount: Int,
    val strategies: List<StrategyItem> = emptyList(),
)

data class StrategyItem(
    val id: String,
    val name: String,
    val instId: String,
    val side: String,
    val contracts: Double,
    val entry: Double,
    val mark: Double,
    val upl: Double,
    val leverage: Int,
    val status: String,
    val source: String,
    val errorMsg: String? = null,
)

data class ApiKeyInfo(
    val label: String,
    val masked: String,
    val permissions: List<String>,
    val ipWhitelist: String?,
    val createdAt: String,
)

data class Candle(val ts: Long, val o: Double, val h: Double, val l: Double, val c: Double)

data class PlaceOrderReq(
    val instId: String,
    val side: String,
    val sz: String,
    val ordType: String = "market",
    val px: String? = null,
)

data class ApiResult<T>(
    val ok: Boolean = false,
    val data: T? = null,
    val message: String? = null,
)
