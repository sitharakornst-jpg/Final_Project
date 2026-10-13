package com.example.afinal.data.local

data class DecisionEntity(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val category: String,
    val chosenResult: String,
    val reasoning: String,
    val timestamp: Long = System.currentTimeMillis()
)
