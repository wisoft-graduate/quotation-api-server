package wisoft.io.quotation.adaptor.`in`.http.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import wisoft.io.quotation.exception.error.UnauthorizedUserException
import wisoft.io.quotation.util.JWTUtil
import wisoft.io.quotation.util.annotation.LoginAuthenticated
import wisoft.io.quotation.util.annotation.RefreshTokenAuthenticated
import wisoft.io.quotation.util.annotation.ResetPasswordAuthenticated

@Component
class AuthInterceptor : HandlerInterceptor {
    val logger = KotlinLogging.logger {}

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (handler !is HandlerMethod) return true
        val loginAuthenticated = handler.getMethodAnnotation(LoginAuthenticated::class.java)
        val refreshTokenAuthenticated = handler.getMethodAnnotation(RefreshTokenAuthenticated::class.java)
        val resetPasswordAuthenticated = handler.getMethodAnnotation(ResetPasswordAuthenticated::class.java)

        if (loginAuthenticated != null) {
            return loginAuthCheck(request)
        }
        if (resetPasswordAuthenticated != null) {
            return resetPasswordAuthCheck(request)
        }
        if (refreshTokenAuthenticated != null) {
            return refreshTokenCheck(request)
        }
        return true
    }

    private fun loginAuthCheck(request: HttpServletRequest): Boolean {
        return runCatching {
            val authHeader =
                request.getHeader("Authorization") ?: throw UnauthorizedUserException("Authorization Not Found")
            val token = authHeader.substringAfter("Bearer ")

            JWTUtil.verifyAccessToken(token)

            val userId = JWTUtil.extractUserIdByToken(token)
            request.setAttribute("userId", userId)

            true
        }.onFailure {
            logger.error { "loginAuthCheck fail" }
        }.getOrThrow()
    }

    private fun refreshTokenCheck(request: HttpServletRequest): Boolean {
        return runCatching {
            val authHeader =
                request.getHeader("Authorization") ?: throw UnauthorizedUserException("Authorization Not Found")
            val token = authHeader.substringAfter("Bearer ")

            val userId = JWTUtil.extractUserIdByToken(token)
            request.setAttribute("userId", userId)
            true
        }.onFailure {
            println(4444)
            logger.error { "refreshTokenCheck fail" }
        }.getOrThrow()
    }

    private fun resetPasswordAuthCheck(request: HttpServletRequest): Boolean {
        return runCatching {
            val authHeader =
                request.getHeader("Authorization") ?: throw UnauthorizedUserException("Authorization Not Found")
            val token = authHeader.substringAfter("Bearer ")

            JWTUtil.verifyResetPasswordToken(token)

            val userId = JWTUtil.extractUserIdByToken(token)
            request.setAttribute("userId", userId)

            true
        }.onFailure {
            logger.error { "resetPasswordAuthCheck fail" }
        }.getOrThrow()
    }
}
