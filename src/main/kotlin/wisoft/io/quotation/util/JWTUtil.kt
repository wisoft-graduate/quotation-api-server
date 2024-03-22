package wisoft.io.quotation.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import wisoft.io.quotation.domain.User
import java.util.*

@Component
class JWTUtil {
    @Value("\${environment.jwt.secret-key}")
    lateinit var secretKey: String

    @Value("\${environment.jwt.access-token-expiration-time}")
    var accessTokenExpirationTime: Int = 0

    @Value("\${environment.jwt.refresh-token-expiration-time}")
    var refreshTokenExpirationTime: Int = 0


    fun generateAccessToken(user: User): String {
        val expirationDate = Date(System.currentTimeMillis() + accessTokenExpirationTime)
        return Jwts.builder()
            .setSubject(user.nickname)
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun generateRefreshToken(user: User): String {
        val expirationDate = Date(System.currentTimeMillis() + refreshTokenExpirationTime)
        return Jwts.builder()
            .setSubject(user.nickname)
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

    fun verifyToken(token: String, currentDate: Date = Date()): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            claims.body.expiration.after(currentDate)

        } catch (e: Exception) {
            println(e)
            false
        }

    }
}