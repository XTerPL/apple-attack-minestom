package org.joebobilly.appleattack.utils

object CommandUtils {
    fun List<String>.filterByCommandInput(input: String, preprocess: (String) -> String = { it })
        = this.filter { preprocess(it).startsWith(preprocess(input)) }
}