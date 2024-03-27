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
import wisoft.io.quotation.exception.error.ErrorMessage
import wisoft.io.quotation.exception.error.QuotationNotFoundException

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorMessage> {
        val status = BAD_REQUEST
        val errorMessage = ErrorMessage.from(status, e, request)

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }

    @ExceptionHandler(QuotationNotFoundException::class)
    fun quotationNotFoundExceptionHandler(
        e: QuotationNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorMessage> {
        val status = NOT_FOUND
        val errorMessage = ErrorMessage.from(status, e, request)

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }
}