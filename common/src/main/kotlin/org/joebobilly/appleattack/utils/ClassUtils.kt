package org.joebobilly.appleattack.utils

import kotlin.reflect.KClass

object ClassUtils {
    inline fun <reified T : Any> getKClass(): KClass<T> {
        return T::class
    }
}