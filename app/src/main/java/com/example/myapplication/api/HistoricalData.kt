package com.example.myapplication.api

data class HistoricalData(
    val market_caps: List<List<Any>>,
    val prices: List<List<Double>>,
    val total_volumes: List<List<Any>>
)