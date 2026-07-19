package org.joebobilly.appleattack.utils

class NBTReadError : IllegalArgumentException {
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
        if(super.message == null) {
            return "$source: Unknown error"
        }
        return source + ": " + super.message
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