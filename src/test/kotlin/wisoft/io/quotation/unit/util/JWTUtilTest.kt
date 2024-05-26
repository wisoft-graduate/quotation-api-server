package wisoft.io.quotation.unit.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class JWTUtilTest : FunSpec({

    test("readYamlFile 성공") {
        // given
        val jwtUtil = wisoft.io.quotation.util.JWTUtil

        // when
        val yaml = jwtUtil.readYmlFile()

        // then
        yaml.environment.jwt.secretKey shouldNotBe null
        yaml.environment.jwt.refreshTokenExpirationTime shouldNotBe null
        yaml.environment.jwt.passwordRefreshTokenExpirationTime shouldNotBe null
    }
})
