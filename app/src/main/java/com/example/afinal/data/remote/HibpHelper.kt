package com.example.afinal.data.remote

import java.security.MessageDigest

object HibpHelper {

    suspend fun checkPasswordBreach(password: String): Int {
        if (password.isEmpty()) return 0
        return try {
            val sha1Hex = sha1(password).uppercase()
            val prefix = sha1Hex.substring(0, 5)
            val suffix = sha1Hex.substring(5)

            val response = RetrofitClient.hibpApiService.checkPwnedRange(prefix)
            if (!response.isSuccessful) return 0

            val bodyText = response.body()?.string() ?: return 0

            bodyText.lineSequence().forEach { line ->
                val parts = line.split(":")
                if (parts.size == 2) {
                    val lineSuffix = parts[0].trim().uppercase()
                    if (lineSuffix == suffix) {
                        return parts[1].trim().toIntOrNull() ?: 0
                    }
                }
            }
            0
        } catch (e: Exception) {
            0
        }
    }

    private fun sha1(input: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
