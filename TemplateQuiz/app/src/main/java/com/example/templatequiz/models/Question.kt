package com.example.templatequiz.models

data class Question(
    val id: Int,
    val question: String,
    val answers: List<String>,
    val correctIndex: Int
)