package com.example.myapplication.api

data class Result(
    val content: String,
    val creator: List<String>,
    val description: String,
    val full_description: String,
    val image_url: Any,
    val keywords: List<String>,
    val link: String,
    val pubDate: String,
    val source_id: String,
    val title: String,
    val video_url: Any
)