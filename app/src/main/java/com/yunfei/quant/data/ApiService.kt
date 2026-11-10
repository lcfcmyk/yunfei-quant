package com.yunfei.quant.data

import retrofit2.http.*

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body req: LoginReq): ApiResult<LoginResp>

    @POST("api/register")
    suspend fun register(@Body req: RegisterReq): ApiResult<LoginResp>

    @GET("api/overview")
    suspend fun overview(@Header("Authorization") token: String): ApiResult<Overview>

    @GET("api/strategies")
    suspend fun strategies(@Header("Authorization") token: String): ApiResult<List<StrategyItem>>

    @POST("api/strategies/{id}/toggle")
    suspend fun toggleStrategy(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("running") running: Boolean,
    ): ApiResult<Unit>

    @POST("api/strategies/pause-all")
    suspend fun pauseAll(@Header("Authorization") token: String): ApiResult<Unit>

    @GET("api/candles")
    suspend fun candles(
        @Header("Authorization") token: String,
        @Query("instId") instId: String,
        @Query("bar") bar: String = "15m",
        @Query("limit") limit: Int = 200,
    ): ApiResult<List<Candle>>

    @POST("api/order")
    suspend fun placeOrder(
        @Header("Authorization") token: String,
        @Body req: PlaceOrderReq,
    ): ApiResult<String>

    @GET("api/apikey")
    suspend fun apiKeyInfo(@Header("Authorization") token: String): ApiResult<ApiKeyInfo>

    @GET("api/logs")
    suspend fun logs(
        @Header("Authorization") token: String,
        @Query("lines") lines: Int = 100,
    ): ApiResult<List<String>>
}
