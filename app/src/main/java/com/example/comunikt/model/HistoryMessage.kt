package com.example.comunikt.model

data class HistoryMessage(
    val id: String,
    val text: String,
    val type: String,
    val createdAt: Long,
)