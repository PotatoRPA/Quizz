package com.example.templatequiz.models

import com.example.templatequiz.additional.AnswerState

data class AnswerItem(
    val number: Int,
    val state: AnswerState
)