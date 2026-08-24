package com.example.comunikt.model

data class User(
    val name: String,
    val email: String,
    val password: String,
    val profileType: String,
    val communicationMode: String,
)