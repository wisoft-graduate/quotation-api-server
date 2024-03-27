package wisoft.io.quotation.exception.error.http

open class NotFoundException(
    open val value: String
) : Throwable(
    message = value + HttpMessage.HTTP_404.message
)