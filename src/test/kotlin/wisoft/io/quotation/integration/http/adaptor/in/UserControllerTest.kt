package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Testcontainers
import wisoft.io.quotation.DatabaseContainerConfig
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.CreateUserUseCase
import wisoft.io.quotation.application.port.`in`.GetUserUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.http.HttpMessage
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import wisoft.io.quotation.util.JWTUtil

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class UserControllerTest(
    val mockMvc: MockMvc, val repository: UserRepository, val jwtUtil: JWTUtil
) : FunSpec({

    val objectMapper = ObjectMapper().registerKotlinModule()

    afterEach {
        repository.deleteAll()
    }

    context("createUser Test") {
        test("createUser 성공") {
            // given
            val request = CreateUserUseCase.CreateUserRequest(
                id = "user123",
                password = "password",
                nickname = "nickname",
                identityVerificationQuestion = "question",
                identityVerificationAnswer = "answer"
            )

            // when
            val signUpRequestJson = objectMapper.writeValueAsString(request)
            val result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(signUpRequestJson)
            )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()
                .response.contentAsString

            // then
            val actual = objectMapper.readValue(result, CreateUserUseCase.CreateUserResponse::class.java)
            actual.data.id shouldBe request.id
        }
        test("createUser 실패 - 아이디 중복") {
            // given
            val expectStatus = HttpMessage.HTTP_400.status

            val existUser = repository.save(getUserEntityFixture())
            val request = CreateUserUseCase.CreateUserRequest(
                id = existUser.id,
                password = "password",
                nickname = "nickname",
                identityVerificationQuestion = "question",
                identityVerificationAnswer = "answer"
            )

            // when
            val createUserRequestJson = objectMapper.writeValueAsString(request)
            val result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createUserRequestJson)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andReturn()
                .response.contentAsString

            // then
            val actual = objectMapper.readValue(result, ErrorData::class.java)
            actual.data.status shouldBe expectStatus.value()
        }
    }

    context("signIn Test") {
        test("signIn 성공") {
            // given
            val existUser = repository.save(getUserEntityFixture())
            val request = SignInUseCase.SignInRequest(
                existUser.id, "password"
            )

            // when
            val signInRequestJson = objectMapper.writeValueAsString(request)
            val result = mockMvc.perform(
                MockMvcRequestBuilders.post("/users/sign-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(signInRequestJson)
            ).andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
                .response.contentAsString

            // then
            val actual = objectMapper.readValue(result, SignInUseCase.SignInResponse::class.java)

            val userIdByAccessToken = jwtUtil.extractUserIdByToken(actual.data.accessToken)
            val userIdByRefreshToken = jwtUtil.extractUserIdByToken(actual.data.refreshToken)
            userIdByAccessToken shouldBe existUser.nickname
            userIdByRefreshToken shouldBe existUser.nickname
        }
    }

    context("getUserList Test") {
        test("getUserList 성공") {
            // given
            val existUser = repository.save(getUserEntityFixture())
            val request = GetUserUseCase.GetUserByIdOrNicknameRequest(
                existUser.id, null
            )

            // when
            val result = mockMvc.get("/users") {
                param("id", request.id!!)
                accept = MediaType.APPLICATION_JSON
            }
                .andExpect { MockMvcResultMatchers.status().isOk }
                .andReturn()
                .response.contentAsString

            // then
            val actual = objectMapper.readValue(result, GetUserUseCase.GetUserByIdOrNicknameResponse::class.java)
            actual.data.id shouldBe existUser.id
            actual.data.nickname shouldBe existUser.nickname
        }
    }

    context("deleteUser Test") {
        test("deleteUser 성공") {
            // given
            val existUser = repository.save(getUserEntityFixture())

            val result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/${existUser.id}")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
            // TODO : 반환하지 않더라도, DB 조회해서 확인 여부
        }
    }

})
