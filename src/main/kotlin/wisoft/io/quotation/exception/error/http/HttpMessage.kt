package wisoft.io.quotation.exception.error.http

import org.springframework.http.HttpStatus

enum class HttpMessage(
    val status: HttpStatus,
    val message: String,
) {
    HTTP_400(HttpStatus.BAD_REQUEST, " 잘못된 요청입니다."),
    HTTP_401(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    HTTP_403(HttpStatus.FORBIDDEN, "사용자는콘텐츠에 접근할 권리를 가지고 있지 않습니다."),
    HTTP_404(HttpStatus.NOT_FOUND, " 리소스가 존재하지 않습니다."),
}
