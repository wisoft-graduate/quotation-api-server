package wisoft.io.quotation.exception.error.http

import org.springframework.http.HttpStatus

enum class HttpMessage(
    val status: HttpStatus,
    val message: String
) {
    HTTP_400(HttpStatus.BAD_REQUEST, " 잘못된 요청입니다."),
    HTTP_404(HttpStatus.NOT_FOUND, " 리소스가 존재하지 않습니다.")
}