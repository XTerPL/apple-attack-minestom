package org.joebobilly.appleattack.utils

object NumberUtils {
    private val roman = listOf(
        Pair("M", 1000),
        Pair("CM", 900),
        Pair("D", 500),
        Pair("CD", 400),
        Pair("C", 100),
        Pair("XC", 90),
        Pair("L", 50),
        Pair("XL", 40),
        Pair("X", 10),
        Pair("IX", 9),
        Pair("V", 5),
        Pair("IV", 4),
        Pair("I", 1),
    )

    fun toRoman(number: Int): String {
        if(number == 0) return "0"
        if(number < 0) return "-" + toRoman(-number)

        var result = ""
        var number = number
        for(pair in roman) {
            val q = (number / pair.second)
            number -= q * pair.second
            result += pair.first.repeat(q)
        }

        return result
    }
}