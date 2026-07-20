package org.joebobilly.appleattack.utils

import java.util.Locale

object StringUtils {
    fun String.titlecase(locale: Locale): String {
        return this.substring(0, 1).uppercase(locale) + this.substring(1).lowercase(locale)
    }
    fun String.titlecase(): String {
        return this.titlecase(Locale.ROOT)
    }
}