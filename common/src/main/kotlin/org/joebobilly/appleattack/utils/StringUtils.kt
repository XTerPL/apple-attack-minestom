package org.joebobilly.appleattack.utils

import java.util.Locale

object StringUtils {
    fun String.titlecase(locale: Locale): String {
        return this.substring(0, 1).uppercase(locale) + this.substring(1).lowercase(locale)
    }
    fun String.titlecase(): String {
        return this.titlecase(Locale.ROOT)
    }
    fun List<String>.joinWithEscape(delimiter: Char = ':', escaper: Char = '\\'): String {
        val builder = StringBuilder()
        for(part in this) {
            if(builder.isNotEmpty()) builder.append(delimiter)
            for(char in part) {
                if(char == escaper || char == delimiter) {
                    builder.append(escaper)
                }
                builder.append(char)
            }
        }
        return builder.toString()
    }
    fun String.splitEscaped(delimiter: Char = ':', escaper: Char = '\\'): List<String> {
        val parts = mutableListOf<String>()
        val builder = StringBuilder()
        var escaped = false
        for(char in this) {
            if(!escaped) {
                if(char == escaper) {
                    escaped = true
                    continue
                }
                if(char == delimiter) {
                    parts.add(builder.toString())
                    builder.clear()
                    continue
                }
            }
            builder.append(char)
            escaped = false
        }
        parts.add(builder.toString())
        return parts.toList()
    }
}