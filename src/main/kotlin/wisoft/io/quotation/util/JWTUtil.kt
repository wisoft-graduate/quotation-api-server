package wisoft.io.quotation.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import wisoft.io.quotation.domain.User
import java.util.*

object JWTUtil {
    private const val SECRET_KEY = "8kTcEnj4ABscsjcWVOg06brYjyOwwVwdMB7s1BGwzBQDhmASi"
    private const val ACCESS_TOKEN_EXPIRATION_TIME = 3600000 // 토큰 만료 시간 (1시간)
    private const val REFRESH_TOKEN_EXPIRATION_TIME = 604800000 // 토큰 만료 시간 (1주일)

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