package org.joebobilly.appleattack.serialization

class NBTReadError : IllegalArgumentException {
    companion object {
        fun checkOrThrow(condition: Boolean, source: String, lazyMessage: () -> String) {
            if(!condition) throw NBTReadError(source, lazyMessage())
        }
        fun checkOrThrow(condition: Boolean, lazyMessage: () -> String) {
            checkOrThrow(condition, "", lazyMessage)
        }

        fun <T> wrap(source: String, consumer: () -> T): T {
            try {
                return consumer()
            }
            catch(e : NBTReadError) {
                e.addSource(source)
                throw e
            }
            catch(e : Exception) {
                throw NBTReadError(source, e.message ?: "", e)
            }
        }
    }

    var source: String

    constructor(source: String, message: String) : super(message) {
        this.source = source
    }
    constructor(source: String, cause: Throwable) : super(cause) {
        this.source = source
    }
    constructor(source: String, message: String, cause: Throwable) : super(message, cause) {
        this.source = source
    }

    fun getSourcedMessage(): String {
        return source.ifEmpty { "<unknown>" } + ": " + (super.message ?: "Unknown error")
    }

    fun addSource(source: String): NBTReadError {
        if(this.source.isEmpty()) {
            this.source = source
            return this
        }
        this.source = "$source.${this.source}"
        return this
    }
}