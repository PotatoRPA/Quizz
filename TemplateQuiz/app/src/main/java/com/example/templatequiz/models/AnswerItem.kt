package com.example.templatequiz.models

import com.example.templatequiz.additional.AnswerState

data class AnswerItem(
    val questionNumber: Int,
    val answerState: AnswerState
)