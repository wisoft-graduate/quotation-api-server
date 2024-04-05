package wisoft.io.quotation.exception.handler

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity.BodyBuilder
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.ErrorMessage
import wisoft.io.quotation.exception.error.QuotationNotFoundException
import wisoft.io.quotation.exception.error.UserNotFoundException
import wisoft.io.quotation.exception.error.http.HttpMessage

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_400.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }

    @ExceptionHandler(QuotationNotFoundException::class)
    fun quotationNotFoundExceptionHandler(
        e: QuotationNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_404.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun userNotFoundExceptionHandler(
        e: UserNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_404.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }
}