package wisoft.io.quotation.exception.error

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import java.lang.Exception
import java.time.LocalDateTime

data class ErrorMessage(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val path: String,
    var message: String
) {
    companion object {
        fun from(httpStatus: HttpStatus, exception: Exception, request: HttpServletRequest): ErrorMessage {
            return ErrorMessage(
                status = httpStatus.value(),
                error = httpStatus.name,
                path = request.requestURI,
                message = when (exception) {
                    is MethodArgumentNotValidException -> {
                        exception
                            .bindingResult
                            .fieldErrors
                            .joinToString {
                                "[${it.field}: ${it.defaultMessage.toString()}]"
                            }
                    }

                    else -> {
                        exception.message ?: "Unknown error occurred."
                    }
                }
            )
        }
    }
}
