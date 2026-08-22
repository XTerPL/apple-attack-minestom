package org.joebobilly.appleattack.utils

object CollectionUtils {
    fun <T> Set<T>.powerSet(): Set<Set<T>> {
        val list = this.toList()
        val contains = Array<Boolean>(this.size) { false }
        val result = mutableSetOf<Set<T>>()
        var adding: Boolean
        do {
            val current = mutableSetOf<T>()
            adding = true
            for(i in contains.indices) {
                if(adding) {
                    contains[i] = !contains[i]
                }
                if(contains[i]) {
                    adding = false
                    current.add(list[i])
                }
            }
            result.add(current)
        } while(!adding)
        return result.toSet()
    }
    fun <T, R> Pair<T, T>.map(mapper: (T) -> R): Pair<R, R> {
        return mapper(first) to mapper(second)
    }
}