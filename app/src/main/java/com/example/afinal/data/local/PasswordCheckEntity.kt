package com.example.afinal.data.local

data class PasswordCheckEntity(
    val id: Long = System.currentTimeMillis(),
    val maskedPassword: String,
    val score: Int,
    val strengthLevel: String,
    val length: Int,
    val breachCount: Int,
    val aiAdvice: String,
    val timestamp: Long = System.currentTimeMillis()
)
