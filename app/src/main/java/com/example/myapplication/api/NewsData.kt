package com.example.myapplication.api

data class NewsData(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)