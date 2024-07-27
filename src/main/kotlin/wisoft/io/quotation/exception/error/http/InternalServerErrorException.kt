package wisoft.io.quotation.exception.error.http

open class InternalServerErrorException(
    open val value: String,
) : Throwable(
        message = value + HttpMessage.HTTP_500.message,
    )
