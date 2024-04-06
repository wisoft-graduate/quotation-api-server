package wisoft.io.quotation.exception.error.http

open class UnauthorizedException(
    open val value: String
) : Throwable(
    message = value + HttpMessage.HTTP_401
)