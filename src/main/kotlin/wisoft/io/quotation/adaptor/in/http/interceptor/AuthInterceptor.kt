package wisoft.io.quotation.adaptor.`in`.http.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import wisoft.io.quotation.exception.error.InvalidJwtTokenException
import wisoft.io.quotation.exception.error.UnauthorizedUserException
import wisoft.io.quotation.util.JWTUtil
import wisoft.io.quotation.util.annotation.Authenticated

@Component
class AuthInterceptor : HandlerInterceptor {
    val logger = KotlinLogging.logger {}
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true
        handler.getMethodAnnotation(Authenticated::class.java) ?: return true

        return runCatching {
            val authToken = request.getHeader("Authorization") ?: throw UnauthorizedUserException("")
            JWTUtil.verifyToken(authToken)
        }.onFailure {
            logger.error { "preHandle fail" }
        }.getOrThrow()
    }
}