package com.example.myapplication.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface ApiInterface {

    @GET("api/v3/coins/{coinData}/market_chart")
    fun getData(@Path(value = "coinData") coinData: String, @Query("vs_currency") vs_currency: String,
                @Query("days") days: String, @Query("interval") interval: String ) :Call<HistoricalData>

    @GET("coins/markets?order=market_cap_desc&per_page=250&page=1&sparkline=false")
    fun getCoins(@Query("vs_currency") vs_currency: Currency): Call<List<CoinDataListItem>>

    @GET("api/v3/coins/{coinName}")
    fun getDescription(@Path(value = "coinName") coinData: String): Call<coinDescriptionData>
}