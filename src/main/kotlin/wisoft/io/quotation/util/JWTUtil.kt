package wisoft.io.quotation.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import wisoft.io.quotation.domain.User
import java.util.*

@Component
class JWTUtil
{
    @Value("\${environment.jwt.secret-key}")
    lateinit var SECRET_KEY: String

    @Value("\${environment.jwt.access-token-expiration-time}")
    var ACCESS_TOKEN_EXPIRATION_TIME: Int = 0

    @Value("\${environment.jwt.refresh-token-expiration-time}")
    var REFRESH_TOKEN_EXPIRATION_TIME: Int = 0


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
}