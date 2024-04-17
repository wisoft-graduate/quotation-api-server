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
import wisoft.io.quotation.application.port.`in`.*
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.http.HttpMessage
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import wisoft.io.quotation.util.JWTUtil
import wisoft.io.quotation.util.SaltUtil

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class UserControllerTest(
    val mockMvc: MockMvc, val repository: UserRepository
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
            val expectedStatus = HttpMessage.HTTP_400.status

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
            actual.data.status shouldBe expectedStatus.value()
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

            val userIdByAccessToken = JWTUtil.extractUserIdByToken(actual.data.accessToken)
            val userIdByRefreshToken = JWTUtil.extractUserIdByToken(actual.data.refreshToken)
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
            val existUserEntity = repository.save(getUserEntityFixture())
            val existUser = existUserEntity.toDomain()
            val accessToken = JWTUtil.generateAccessToken(existUser)

            val result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/${existUserEntity.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer $accessToken")
            )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
        }

        test("deleteUser 실패 - 비 인가된 사용자") {
            // given
            val existUser = repository.save(getUserEntityFixture())

            val result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/${existUser.id}")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
        }

        test("deleteUser 실패 - 인증 정보 불일치 사용자") {
            // given
            val existUser = repository.save(getUserEntityFixture())
            val accessToken = "testToken"

            // when, then
            val result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/${existUser.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", accessToken)
            )
                .andExpect(MockMvcResultMatchers.status().isForbidden)
        }
    }

    context("getUserDetail Test") {
        test("getUserDetail 성공") {
            // given
            val existUser = repository.save(getUserEntityFixture())
            // when
            val result = mockMvc.perform(
                MockMvcRequestBuilders.get("/users/${existUser.id}")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect { MockMvcResultMatchers.status().isOk }
                .andReturn()
                .response.contentAsString

            // then
            val actual = objectMapper.readValue(result, GetUserDetailUseCase.GetUserDetailByIdResponse::class.java)
            actual.data.id shouldBe existUser.id
            actual.data.nickname shouldBe existUser.nickname
            actual.data.bookmarkCount shouldBe 0
            actual.data.likeQuotationCount shouldBe 0
        }
    }

    context("updateUser Test") {
        test("updateUser 성공 ") {
            // given
            val existUser = repository.save(getUserEntityFixture())
            val expectedNickname = "updatedNickname"
            val expectedProfilePath = "updatedProfile"
            val expectedFavoriteQuotation = "updatedFavoriteQuotation"
            val expectedFavoriteAuthor = "updatedFavoriteAuthor"
            val expectedQuotationAlarm = true
            val expectedCommentAlarm = true
            val expectedIdentityVerificationQuestion = "updatedIdentityVerificationQuestion"
            val expectedIdentityVerificationAnswer = "updatedIdentityVerificationAnswer"
            val request = UpdateUserUseCase.UpdateUserRequest(
                nickname = expectedNickname,
                profilePath = expectedProfilePath,
                favoriteQuotation = expectedFavoriteQuotation,
                favoriteAuthor = expectedFavoriteAuthor,
                quotationAlarm = expectedQuotationAlarm,
                commentAlarm = expectedCommentAlarm,
                identityVerificationQuestion = expectedIdentityVerificationQuestion,
                identityVerificationAnswer = expectedIdentityVerificationAnswer,
            )
            val accessToken = JWTUtil.generateAccessToken(existUser.toDomain())
            // when
            val requestToJson = objectMapper.writeValueAsString(request)
            val result = mockMvc.perform(
                MockMvcRequestBuilders.put("/users/${existUser.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer $accessToken")
                    .content(requestToJson)
            ).andExpect(
                MockMvcResultMatchers.status().isOk
            ).andReturn()
                .response.contentAsString

            // then
            val actual = objectMapper.readValue(result, UpdateUserUseCase.UpdateUserResponse::class.java)
            actual.data.id shouldBe existUser.id

            val actualUser = repository.findById(existUser.id).get()

            actualUser.nickname shouldBe expectedNickname
            actualUser.profilePath shouldBe expectedProfilePath
            actualUser.favoriteQuotation shouldBe expectedFavoriteQuotation
            actualUser.favoriteAuthor shouldBe expectedFavoriteAuthor
            actualUser.quotationAlarm shouldBe expectedQuotationAlarm
            actualUser.commentAlarm shouldBe expectedCommentAlarm
            actualUser.identityVerificationQuestion shouldBe expectedIdentityVerificationQuestion
            actualUser.identityVerificationAnswer shouldBe expectedIdentityVerificationAnswer


        }
    }


})
