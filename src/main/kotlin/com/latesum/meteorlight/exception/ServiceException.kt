package com.latesum.meteorlight.exception

class ServiceException(message: String?, type: ExceptionType) : Exception(message) {

    enum class ExceptionType {
        UNKNOWN,
        PERMISSION_DENIED,
        INVALID_ARGUMENT
    }

    var type: ExceptionType = ExceptionType.UNKNOWN

    init {
        this.type = type
    }

    private constructor(builder: Builder) : this(message = builder.message, type = builder.type)

    companion object {
        fun newBuilder(): Builder {
            return Builder()
        }
    }

    class Builder {
        var message: String? = null

        var type: ExceptionType = ExceptionType.UNKNOWN

        fun setType(type: ExceptionType): Builder {
            this.type = type
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun build() = ServiceException(this)
    }

}
