package wisoft.io.quotation.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mu.KotlinLogging
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.exception.error.InvalidJwtTokenException
import java.util.*

object JWTUtil {
    private val logger = KotlinLogging.logger {}

    fun generateAccessToken(user: User): String {
        return runCatching {
            val jwtConfig = this.readYmlFile().environment.jwt
            val expirationDate = Date(System.currentTimeMillis() + jwtConfig.accessTokenExpirationTime)

            val claims: Claims =
                Jwts.claims()
                    .setSubject(user.id)
                    .setIssuedAt(Date())
                    .setExpiration(expirationDate)
            claims.put("type", "accessToken")

            Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.secretKey)
                .compact()
        }
            .onFailure {
                logger.error { "generateAccessToken fail: param[$user]" }
            }.getOrThrow()
    }

    fun generateRefreshToken(user: User): String {
        return runCatching {
            val jwtConfig = this.readYmlFile().environment.jwt
            val expirationDate = Date(System.currentTimeMillis() + jwtConfig.refreshTokenExpirationTime)

            val claims: Claims =
                Jwts.claims()
                    .setSubject(user.id)
                    .setIssuedAt(Date())
                    .setExpiration(expirationDate)
            claims.put("type", "refreshToken")

            Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.secretKey)
                .compact()
        }.onFailure {
            logger.error { "generateRefreshToken fail: param[$user]" }
        }.getOrThrow()
    }

    fun generatePasswordResetToken(user: User): String {
        return runCatching {
            val jwtConfig = this.readYmlFile().environment.jwt
            val expirationDate = Date(System.currentTimeMillis() + jwtConfig.passwordRefreshTokenExpirationTime)

            val claims: Claims =
                Jwts.claims()
                    .setSubject(user.id)
                    .setIssuedAt(Date())
                    .setExpiration(expirationDate)
            claims.put("type", "resetPasswordToken")

            Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.secretKey)
                .compact()
        }
            .onFailure {
                logger.error { "generatePasswordResetToken fail: param[$user]" }
            }.getOrThrow()
    }

    fun verifyAccessToken(
        token: String,
        currentDate: Date = Date(),
    ): Boolean {
        val jwtConfig = this.readYmlFile().environment.jwt
        return runCatching {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.secretKey)
                    .build()
                    .parseClaimsJws(token)

            val tokenType = claims.body["type"].toString()
            if (tokenType != "accessToken") throw InvalidJwtTokenException("tokenType: $tokenType")

            claims.body.expiration.after(currentDate)
        }.onFailure {
            logger.error { "verifyToken fail: param[token: $token]" }
            throw InvalidJwtTokenException(it.toString())
        }.getOrThrow()
    }

    fun verifyRefreshToken(
        token: String,
        currentDate: Date = Date(),
    ): Boolean {
        val jwtConfig = this.readYmlFile().environment.jwt
        return runCatching {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.secretKey)
                    .build()
                    .parseClaimsJws(token)

            val tokenType = claims.body["type"].toString()
            if (tokenType != "refreshToken") throw InvalidJwtTokenException("tokenType: $tokenType")

            claims.body.expiration.after(currentDate)
        }.onFailure {
            logger.error { "verifyToken fail: param[token: $token]" }
            throw InvalidJwtTokenException(it.toString())
        }.getOrThrow()
    }

    fun verifyResetPasswordToken(
        token: String,
        currentDate: Date = Date(),
    ): Boolean {
        val jwtConfig = this.readYmlFile().environment.jwt
        return runCatching {
            val claims =
                Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.secretKey)
                    .build()
                    .parseClaimsJws(token)

            val tokenType = claims.body["type"].toString()
            if (tokenType != "resetPasswordToken") throw InvalidJwtTokenException("tokenType: $tokenType")

            claims.body.expiration.after(currentDate)
        }.onFailure {
            logger.error { "verifyToken fail: param[token: $token]" }
            throw InvalidJwtTokenException(it.toString())
        }.getOrThrow()
    }

    fun extractUserIdByToken(token: String): String {
        return runCatching {
            val jwtConfig = this.readYmlFile().environment.jwt
            Jwts.parserBuilder()
                .setSigningKey(jwtConfig.secretKey)
                .build()
                .parseClaimsJws(token).body.subject.toString()
        }.onFailure {
            logger.error { "verifyToken fail: param[token: $token]" }
        }.getOrThrow()
    }

    fun readYmlFile(): YmlConfig {
        return runCatching {
            val objectMapper = ObjectMapper(YAMLFactory())
            val file = object {}.javaClass.getResource("/application.yaml")

            objectMapper.readValue(file, YmlConfig::class.java)
        }.onFailure {
            logger.error { "readYmlFile fail: param[no Param]" }
        }.getOrThrow()
    }
}
