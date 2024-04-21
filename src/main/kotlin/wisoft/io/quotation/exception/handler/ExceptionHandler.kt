package wisoft.io.quotation.exception.handler

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import wisoft.io.quotation.exception.error.*
import wisoft.io.quotation.exception.error.http.*

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_400.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }

    @ExceptionHandler(BadRequestException::class)
    fun badRequestExceptionHandler(
        e: BadRequestException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_400.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }

    @ExceptionHandler(NotFoundException::class)
    fun notFoundExceptionHandler(
        e: NotFoundException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_404.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorizedExceptionExceptionHandler(
        e: UnauthorizedException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_401.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun forbiddenExceptionHandler(
        e: ForbiddenException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorData> {
        val status = HttpMessage.HTTP_403.status
        val errorMessage = ErrorData(ErrorMessage.from(status, e, request))

        return ResponseEntity
            .status(status)
            .body(errorMessage)
    }
}
