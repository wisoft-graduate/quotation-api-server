package wisoft.io.quotation.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mu.KotlinLogging
import wisoft.io.quotation.domain.User
import java.io.File
import java.util.*

object JWTUtil {

    private val logger = KotlinLogging.logger {}

    fun generateAccessToken(user: User): String {
        return runCatching {
            val jwtConfig = this.readYmlFile().environment.jwt
            val expirationDate = Date(System.currentTimeMillis() + jwtConfig.accessTokenExpirationTime)
            Jwts.builder()
                .setSubject(user.nickname)
                .setIssuedAt(Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.secretKey)
                .compact()
        }
            .onFailure {
                logger.error { "generateAccessToken fail: param[${user}]" }
            }.getOrThrow()
    }

    fun generateRefreshToken(user: User): String {
        return runCatching {
            val jwtConfig = this.readYmlFile().environment.jwt
            val expirationDate = Date(System.currentTimeMillis() + jwtConfig.refreshTokenExpirationTime)
            Jwts.builder()
                .setSubject(user.nickname)
                .setIssuedAt(Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.secretKey)
                .compact()
        }.onFailure {
            logger.error { "generateRefreshToken fail: param[${user}]" }
        }.getOrThrow()

    }

    fun verifyToken(token: String, currentDate: Date = Date()): Boolean {
        val jwtConfig = this.readYmlFile().environment.jwt
        return runCatching {
            val claims = Jwts.parserBuilder()
                .setSigningKey(jwtConfig.secretKey)
                .build()
                .parseClaimsJws(token)
            claims.body.expiration.after(currentDate)
        }.onFailure {
            logger.error { "verifyToken fail: param[token: ${token}]" }
        }.getOrThrow()
    }

    fun extractUserIdByToken(token: String, currentDate: Date = Date()): String {
        return runCatching {
            val jwtConfig = this.readYmlFile().environment.jwt
            Jwts.parserBuilder()
                .setSigningKey(jwtConfig.secretKey)
                .build()
                .parseClaimsJws(token).body.subject.toString()
        }.onFailure {
            logger.error { "verifyToken fail: param[token: ${token}]" }
        }.getOrThrow()
    }

    private fun readYmlFile(): YmlConfig {
        return runCatching {
            val objectMapper = ObjectMapper(YAMLFactory())
            val file = File("./src/main/resources/application.yaml")

            objectMapper.readValue(file, YmlConfig::class.java)
        }.onFailure {
            logger.error { "readYmlFile fail: param[no Param]" }
        }.getOrThrow()

    }


}