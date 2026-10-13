package com.example.afinal.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class PasswordCheckRepository(context: Context) {
    private val file = File(context.applicationContext.filesDir, "password_checks.json")
    private val gson = Gson()
    private val _history = MutableStateFlow<List<PasswordCheckEntity>>(emptyList())
    val history: StateFlow<List<PasswordCheckEntity>> = _history.asStateFlow()

    init {
        Thread {
            val loaded = loadFromFile()
            _history.value = loaded
        }.start()
    }

    private fun loadFromFile(): List<PasswordCheckEntity> {
        return try {
            if (file.exists()) {
                val json = file.readText()
                val type = object : TypeToken<List<PasswordCheckEntity>>() {}.type
                gson.fromJson<List<PasswordCheckEntity>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveToFile(list: List<PasswordCheckEntity>) {
        try {
            val json = gson.toJson(list)
            file.writeText(json)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun insert(entry: PasswordCheckEntity) {
        val current = _history.value.toMutableList()
        current.add(0, entry)
        _history.value = current
        saveToFile(current)
    }

    fun delete(id: Long) {
        val current = _history.value.filter { it.id != id }
        _history.value = current
        saveToFile(current)
    }

    fun clearAll() {
        _history.value = emptyList()
        if (file.exists()) file.delete()
    }
}
