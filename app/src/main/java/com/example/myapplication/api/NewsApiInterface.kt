package com.example.myapplication.api

import retrofit2.Call
import retrofit2.http.GET

interface NewsApiInterface {

    @GET("everything?q=cryptocurrency&pageSize=100&sortBy=popularity&apiKey=d9c506af1d244bd8b2f475493d530249")
    fun getData(): Call<NewsData>
}