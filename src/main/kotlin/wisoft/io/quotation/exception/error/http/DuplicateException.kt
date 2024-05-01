package wisoft.io.quotation.exception.error.http

open class DuplicateException(
    open val value: String,
) : Throwable(
        message = value + HttpMessage.HTTP_400_DUPLICATE.message,
    )
