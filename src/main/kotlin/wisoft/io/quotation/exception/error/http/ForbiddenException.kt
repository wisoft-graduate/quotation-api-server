package wisoft.io.quotation.exception.error.http

open class ForbiddenException(
    open val value: String
) : Throwable(
    message = value + HttpMessage.HTTP_403
)