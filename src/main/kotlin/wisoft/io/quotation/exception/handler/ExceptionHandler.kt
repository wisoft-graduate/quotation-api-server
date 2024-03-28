package wisoft.io.quotation.exception.handler

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.HttpStatus.BAD_REQUEST
import wisoft.io.quotation.exception.error.ErrorMessage

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorMessage> {
        val status = BAD_REQUEST
        val errorMessage = ErrorMessage.from(status, e, request)

        return ResponseEntity.status(BAD_REQUEST).body(errorMessage)
    }
}