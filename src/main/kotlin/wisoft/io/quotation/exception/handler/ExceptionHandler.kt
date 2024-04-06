package wisoft.io.quotation.exception.handler

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import wisoft.io.quotation.exception.error.*
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

    @ExceptionHandler(InvalidRequestParameterException::class)
    fun invalidRequestParameterExceptionHandler(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_400.status
        val errorMessage = ErrorData(ErrorMessage.from(status,e,request))

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

    @ExceptionHandler(UserDuplicateException::class)
    fun userDuplicateExceptionHandler(
        e: UserDuplicateException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_400.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }
}