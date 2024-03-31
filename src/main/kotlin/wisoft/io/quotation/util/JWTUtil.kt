package wisoft.io.quotation.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import wisoft.io.quotation.domain.User
import java.util.*

@Component
class JWTUtil {
    @Value("\${environment.jwt.secret-key}")
    lateinit var SECRET_KEY: String

    @Value("\${environment.jwt.access-token-expiration-time}")
    var ACCESS_TOKEN_EXPIRATION_TIME: Int = 0

    @Value("\${environment.jwt.refresh-token-expiration-time}")
    var REFRESH_TOKEN_EXPIRATION_TIME: Int = 0

    val logger = KotlinLogging.logger {}


    fun generateAccessToken(user: User): String {
        val expirationDate = Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)
        return Jwts.builder()
            .setSubject(user.nickname)
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

    fun generateRefreshToken(user: User): String {
        val expirationDate = Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME)
        return Jwts.builder()
            .setSubject(user.nickname)
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

    fun verifyToken(token: String, currentDate: Date = Date()): Boolean {
        return runCatching {
            val claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
            claims.body.expiration.after(currentDate)
        }.onFailure {
            logger.error { "verifyToken fail : param[token: ${token}]" }
        }.getOrThrow()
    }

    fun extractUserIdByToken(token: String, currentDate: Date = Date()): String {
        return runCatching {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token).body.subject.toString()
        }.onFailure {
            logger.error { "verifyToken fail : param[token: ${token}]" }
        }.getOrThrow()
    }
}