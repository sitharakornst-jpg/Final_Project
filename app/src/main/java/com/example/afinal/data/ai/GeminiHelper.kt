package com.example.afinal.data.ai

import com.google.ai.client.generativeai.GenerativeModel

class GeminiHelper(private val apiKey: String) {

    suspend fun analyzePasswordSecurity(
        length: Int,
        score: Int,
        strengthLevel: String,
        entropyBits: Double,
        breachCount: Int,
        hasUpper: Boolean,
        hasLower: Boolean,
        hasDigit: Boolean,
        hasSymbol: Boolean,
        warnings: List<String>
    ): String {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isBlank() || trimmedKey == "YOUR_API_KEY") {
            return fallbackAdvice(score, breachCount, warnings)
        }

        val prompt = """
            You are a Cybersecurity Expert & Password Analyst.
            Please analyze the following password structure (evaluating its pattern for security):
            - Security Score: $score / 100 ($strengthLevel)
            - Length: $length characters
            - Entropy: ${"%.1f".format(entropyBits)} bits
            - Data Breach History (HIBP): ${if (breachCount > 0) "Found in data breaches $breachCount times ⚠️" else "No breaches found ✅"}
            - Composition: Uppercase=$hasUpper, Lowercase=$hasLower, Digits=$hasDigit, Symbols=$hasSymbol
            - Warnings: ${if (warnings.isNotEmpty()) warnings.joinToString(", ") else "None"}

            Provide advice in English. Make it concise, easy to understand, and reliable. Organize into 3 sections:
            1. 🔍 Strengths & Weaknesses Evaluation: ...
            2. 🚨 Risks of Data Breach / Brute Force: ...
            3. 💡 Recommendations to Improve Security: ...
        """.trimIndent()

        // Check if API Key is OpenRouter / TinyFish (sk-...) or Custom Server (AQ...)
        if (trimmedKey.startsWith("sk-")) {
            return try {
                val openRouter = OpenRouterHelper(trimmedKey)
                openRouter.analyzePasswordSecurity(prompt)
            } catch (e: Exception) {
                try {
                    val tinyFish = TinyFishHelper(trimmedKey)
                    tinyFish.analyzePasswordSecurity(prompt)
                } catch (e2: Exception) {
                    "⚠️ เรียก OpenRouter / AI API ไม่สำเร็จ (${e.localizedMessage})\n\n" + fallbackAdvice(score, breachCount, warnings)
                }
            }
        } else if (trimmedKey.startsWith("AQ.")) {
            // ใช้ Official Gemini SDK สำหรับ Android
            return try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = trimmedKey
                )
                val response = generativeModel.generateContent(prompt)
                response.text ?: fallbackAdvice(score, breachCount, warnings)
            } catch (e: Exception) {
                "⚠️ Failed to call Gemini AI (Official SDK) (${e.localizedMessage})\n\n" + fallbackAdvice(score, breachCount, warnings)
            }
        }

        return try {
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash-latest",
                apiKey = trimmedKey
            )
            val response = generativeModel.generateContent(prompt)
            response.text ?: fallbackAdvice(score, breachCount, warnings)
        } catch (e: Exception) {
            "⚠️ Failed to call Gemini AI (${e.localizedMessage})\n\n" + fallbackAdvice(score, breachCount, warnings)
        }
    }

    private fun fallbackAdvice(score: Int, breachCount: Int, warnings: List<String>): String {
        val sb = StringBuilder()
        sb.append("🔍 Strengths & Weaknesses: Your security score is $score/100\n")
        if (warnings.isNotEmpty()) {
            sb.append("⚠️ Warnings: ${warnings.joinToString("; ")}\n")
        }
        sb.append("\n🚨 Breach Risks: ")
        if (breachCount > 0) {
            sb.append("This password was found in data breaches $breachCount times! Change it immediately.\n")
        } else {
            sb.append("Not found in known data breaches (Have I Been Pwned).\n")
        }
        sb.append("\n💡 Recommendations: Use a length of at least 16 characters. Mix uppercase, lowercase, numbers, and symbols, or use the Password Generator tool.")
        return sb.toString()
    }
}
