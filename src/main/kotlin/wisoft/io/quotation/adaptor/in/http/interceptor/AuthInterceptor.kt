package wisoft.io.quotation.adaptor.`in`.http.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import wisoft.io.quotation.util.JWTUtil
import wisoft.io.quotation.util.annotation.Authenticated

@Component
class AuthInterceptor : HandlerInterceptor {
    @Autowired
    private lateinit var jwtUtil: JWTUtil

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return if (handler is HandlerMethod) {
            val isAuthenticated: Authenticated? = handler.getMethodAnnotation(Authenticated::class.java)
            if (isAuthenticated != null) {
                val authToken = request.getHeader("Authorization")
                if (authToken == null) {
                    false
                } else {
                    jwtUtil.verifyToken(authToken)
                }
            } else {
                true
            }
        } else {
            true
        }
    }
}