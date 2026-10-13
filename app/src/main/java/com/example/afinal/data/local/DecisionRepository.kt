package com.example.afinal.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class DecisionRepository(context: Context) {
    private val file = File(context.applicationContext.filesDir, "decisions_history.json")
    private val gson = Gson()
    private val _decisions = MutableStateFlow<List<DecisionEntity>>(emptyList())
    val decisions: StateFlow<List<DecisionEntity>> = _decisions.asStateFlow()

    init {
        Thread {
            val loaded = loadFromFile()
            _decisions.value = loaded
        }.start()
    }

    private fun loadFromFile(): List<DecisionEntity> {
        return try {
            if (file.exists()) {
                val json = file.readText()
                val type = object : TypeToken<List<DecisionEntity>>() {}.type
                gson.fromJson<List<DecisionEntity>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveToFile(list: List<DecisionEntity>) {
        try {
            val json = gson.toJson(list)
            file.writeText(json)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun insert(decision: DecisionEntity) {
        val current = _decisions.value.toMutableList()
        current.add(0, decision)
        _decisions.value = current
        saveToFile(current)
    }

    fun delete(id: Long) {
        val current = _decisions.value.filter { it.id != id }
        _decisions.value = current
        saveToFile(current)
    }

    fun clearAll() {
        _decisions.value = emptyList()
        if (file.exists()) file.delete()
    }
}
