package com.example.afinal.data.utils

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

object PasswordGenerator {
    private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?"
    private val SIMILAR_CHARS = setOf('i', 'l', '1', 'L', 'o', '0', 'O', 'I')

    fun generate(
        length: Int = 16,
        includeUpper: Boolean = true,
        includeLower: Boolean = true,
        includeDigits: Boolean = true,
        includeSymbols: Boolean = true,
        excludeSimilar: Boolean = true
    ): String {
        val secureRandom = SecureRandom().asKotlinRandom()

        var charPool = ""
        if (includeUpper) charPool += UPPERCASE
        if (includeLower) charPool += LOWERCASE
        if (includeDigits) charPool += DIGITS
        if (includeSymbols) charPool += SYMBOLS

        if (excludeSimilar) {
            charPool = charPool.filterNot { it in SIMILAR_CHARS }
        }

        if (charPool.isEmpty()) {
            charPool = LOWERCASE + DIGITS
        }

        val sb = StringBuilder()

        // Ensure at least one character from each selected category
        if (includeUpper) {
            val upperFiltered = UPPERCASE.filterNot { excludeSimilar && it in SIMILAR_CHARS }
            if (upperFiltered.isNotEmpty()) sb.append(upperFiltered.random(secureRandom))
        }
        if (includeLower) {
            val lowerFiltered = LOWERCASE.filterNot { excludeSimilar && it in SIMILAR_CHARS }
            if (lowerFiltered.isNotEmpty()) sb.append(lowerFiltered.random(secureRandom))
        }
        if (includeDigits) {
            val digitsFiltered = DIGITS.filterNot { excludeSimilar && it in SIMILAR_CHARS }
            if (digitsFiltered.isNotEmpty()) sb.append(digitsFiltered.random(secureRandom))
        }
        if (includeSymbols) {
            val symbolsFiltered = SYMBOLS.filterNot { excludeSimilar && it in SIMILAR_CHARS }
            if (symbolsFiltered.isNotEmpty()) sb.append(symbolsFiltered.random(secureRandom))
        }

        val targetLength = length.coerceIn(6, 64)
        while (sb.length < targetLength) {
            val nextChar = charPool[secureRandom.nextInt(charPool.length)]
            sb.append(nextChar)
        }

        // Shuffle result
        val charArray = sb.toString().toCharArray()
        charArray.shuffle(secureRandom)

        return String(charArray)
    }
}
