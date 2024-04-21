package wisoft.io.quotation.exception.error.http

open class BadRequestException(
    open val value: String,
) : Throwable(message = value + HttpMessage.HTTP_400.message)
