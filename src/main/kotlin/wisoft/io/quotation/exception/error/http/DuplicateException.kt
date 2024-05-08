package wisoft.io.quotation.exception.error.http

open class DuplicateException(
    open val value: String,
) : Throwable(
        message = HttpMessage.HTTP_400_DUPLICATE.message.replace("자원", value),
    )
