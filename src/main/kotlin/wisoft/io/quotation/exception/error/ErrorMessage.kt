package wisoft.io.quotation.exception.error

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import java.sql.Timestamp

data class ErrorMessage(
    val timestamp: Timestamp = Timestamp(System.currentTimeMillis()),
    val status: Int,
    val error: String,
    val path: String,
    var message: String,
) {
    companion object {
        fun from(
            httpStatus: HttpStatus,
            exception: Throwable,
            request: HttpServletRequest,
        ): ErrorMessage {
            return ErrorMessage(
                status = httpStatus.value(),
                error = httpStatus.reasonPhrase,
                path = request.requestURI,
                message =
                    when (exception) {
                        is MethodArgumentNotValidException -> {
                            exception
                                .bindingResult
                                .fieldErrors
                                .joinToString {
                                    "[${it.field}: ${it.defaultMessage}]"
                                }
                        }

                        else -> {
                            exception.message ?: "Unknown error occurred."
                        }
                    },
            )
        }
    }
}
