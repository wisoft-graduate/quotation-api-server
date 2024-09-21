package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Testcontainers
import wisoft.io.quotation.DatabaseContainerConfig
import wisoft.io.quotation.adaptor.out.persistence.mapeer.UserMapper
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.user.*
import wisoft.io.quotation.application.port.out.s3.CreateProfileImagePort
import wisoft.io.quotation.application.port.out.s3.DeleteProfileImagePort
import wisoft.io.quotation.application.port.out.s3.GetProfileImagePort
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.http.HttpMessage
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import wisoft.io.quotation.fixture.entity.getUserEntityFixtureIncludeQuotationAlarmTimes
import wisoft.io.quotation.util.JWTUtil
import java.sql.Timestamp
import java.time.LocalTime

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class UserControllerTest(
    val mockMvc: MockMvc,
    val repository: UserRepository,
    val userMapper: UserMapper,
) : FunSpec({

        val objectMapper = ObjectMapper().registerKotlinModule()

        afterEach {
            repository.deleteAll()
        }

        context("createUser Test") {
            test("createUser 성공") {
                // given
                val request =
                    CreateUserUseCase.CreateUserRequest(
                        id = "user123",
                        password = "password",
                        nickname = "nickname",
                        identityVerificationQuestion = "question",
                        identityVerificationAnswer = "answer",
                        profileImageBase64 = null,
                    )

                // when
                val signUpRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(signUpRequestJson),
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
                val request =
                    CreateUserUseCase.CreateUserRequest(
                        id = existUser.id,
                        password = "password",
                        nickname = "nickname",
                        identityVerificationQuestion = "question",
                        identityVerificationAnswer = "answer",
                        profileImageBase64 = null,
                    )

                // when
                val createUserRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createUserRequestJson),
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
                val request =
                    SignInUseCase.SignInRequest(
                        existUser.id,
                        "password",
                    )

                // when
                val signInRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/sign-in")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(signInRequestJson),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, SignInUseCase.SignInResponse::class.java)

                val userIdByAccessToken = JWTUtil.extractUserIdByToken(actual.data.accessToken)
                val userIdByRefreshToken = JWTUtil.extractUserIdByToken(actual.data.refreshToken)
                userIdByAccessToken shouldBe existUser.id
                userIdByRefreshToken shouldBe existUser.id
            }
        }
        context("RefreshToken Test") {
            test("RefreshToken 성공") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val refreshToken = JWTUtil.generateRefreshToken(userMapper.toDomain(existUser))

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/refresh-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $refreshToken"),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, SignInUseCase.SignInResponse::class.java)

                val userIdByAccessToken = JWTUtil.extractUserIdByToken(actual.data.accessToken)
                val userIdByRefreshToken = JWTUtil.extractUserIdByToken(actual.data.refreshToken)
                userIdByAccessToken shouldBe existUser.id
                userIdByRefreshToken shouldBe existUser.id
            }
        }
        context("deleteUser Test") {
            test("deleteUser 성공") {
                // given
                val existUserEntity = repository.save(getUserEntityFixture())
                val existUser = userMapper.toDomain(existUserEntity)
                val accessToken = JWTUtil.generateAccessToken(existUser)

                mockMvc.perform(
                    MockMvcRequestBuilders.delete("/users/${existUserEntity.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer $accessToken"),
                )
                    .andExpect(MockMvcResultMatchers.status().isNoContent)
            }

            test("deleteUser 실패 - 비 인가된 사용자") {
                // given
                val existUser = repository.save(getUserEntityFixture())

                mockMvc.perform(
                    MockMvcRequestBuilders.delete("/users/${existUser.id}")
                        .contentType(MediaType.APPLICATION_JSON),
                )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            }

            test("deleteUser 실패 - 인증 정보 불일치 사용자") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val accessToken = "testToken"

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.delete("/users/${existUser.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken),
                )
                    .andExpect(MockMvcResultMatchers.status().isForbidden)
            }
        }

        context("getUserMyPage Test") {
            test("getUserMyPage 성공") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val accessToken = JWTUtil.generateAccessToken(userMapper.toDomain(existUser))
                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/${existUser.id}/my-page")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken"),
                    )
                        .andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, GetUserMyPageUseCase.GetUserMyPageResponse::class.java)
                actual.data.id shouldBe existUser.id
                actual.data.nickname shouldBe existUser.nickname
                actual.data.bookmarkCount shouldBe 0
                actual.data.likeQuotationCount shouldBe 0
                actual.data.commentAlarm shouldBe existUser.commentAlarm
                actual.data.quotationAlarm shouldBe existUser.quotationAlarm
            }

            test("getUserMyPage 실패 - 비 인가된 사용자") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/users/${existUser.id}/my-page")
                        .contentType(MediaType.APPLICATION_JSON),
                )
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            }

            test("getUserMyPage 실패 - 인증 정보 불일치 사용자") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val accessToken = "unauthorized Token"
                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/users/${existUser.id}/my-page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer $accessToken"),
                )
                    .andExpect(MockMvcResultMatchers.status().isForbidden)
            }
        }

        context("getUserPage Test") {
            test("getUserPage 성공") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/${existUser.id}")
                            .contentType(MediaType.APPLICATION_JSON),
                    )
                        .andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, GetUserPageUseCase.GetUserPageResponse::class.java)
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
                val expectedFavoriteQuotation = "updatedFavoriteQuotation"
                val expectedFavoriteAuthor = "updatedFavoriteAuthor"
                val expectedQuotationAlarm = true
                val expectedCommentAlarm = true
                val expectedIdentityVerificationQuestion = "updatedIdentityVerificationQuestion"
                val expectedIdentityVerificationAnswer = "updatedIdentityVerificationAnswer"
                val request =
                    UpdateUserUseCase.UpdateUserRequest(
                        nickname = expectedNickname,
                        profileImageBase64 = null,
                        favoriteQuotation = expectedFavoriteQuotation,
                        favoriteAuthor = expectedFavoriteAuthor,
                        quotationAlarm = expectedQuotationAlarm,
                        commentAlarm = expectedCommentAlarm,
                        identityVerificationQuestion = expectedIdentityVerificationQuestion,
                        identityVerificationAnswer = expectedIdentityVerificationAnswer,
                        isProfileImageDelete = null
                    )
                val accessToken = JWTUtil.generateAccessToken(userMapper.toDomain(existUser))
                // when
                val requestToJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.put("/users/${existUser.id}")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken")
                            .content(requestToJson),
                    ).andExpect(
                        MockMvcResultMatchers.status().isOk,
                    ).andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, UpdateUserUseCase.UpdateUserResponse::class.java)
                actual.data.id shouldBe existUser.id

                val actualUser = repository.findById(existUser.id).get()

                actualUser.nickname shouldBe expectedNickname
                actualUser.favoriteQuotation shouldBe expectedFavoriteQuotation
                actualUser.favoriteAuthor shouldBe expectedFavoriteAuthor
                actualUser.quotationAlarm shouldBe expectedQuotationAlarm
                actualUser.commentAlarm shouldBe expectedCommentAlarm
                actualUser.identityVerificationQuestion shouldBe expectedIdentityVerificationQuestion
                actualUser.identityVerificationAnswer shouldBe expectedIdentityVerificationAnswer
                actualUser.lastModifiedTime shouldNotBe null
            }
        }

        context("getUserList Test") {
            test("getUserList 성공 - searchNickname") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val request =
                    GetUserListUseCase.GetUserListRequest(
                        searchNickname = existUser.nickname.dropLast(3),
                        nickname = null,
                        id = null,
                    )
                // when
                val result =
                    mockMvc.get("/users") {
                        param("searchNickname", request.searchNickname!!)
                        accept = MediaType.APPLICATION_JSON
                    }
                        .andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, GetUserListUseCase.GetUserListResponse::class.java)
                val actualUserDto = actual.data.first()

                actualUserDto.id shouldBe existUser.id
                actualUserDto.nickname shouldBe existUser.nickname
                actualUserDto.profilePath shouldBe existUser.profilePath
            }
            test("getUserList 성공 - nickname") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val request =
                    GetUserListUseCase.GetUserListRequest(
                        searchNickname = null,
                        nickname = existUser.nickname,
                        id = null,
                    )
                // when
                val result =
                    mockMvc.get("/users") {
                        param("nickname", request.nickname!!)
                        accept = MediaType.APPLICATION_JSON
                    }
                        .andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, GetUserListUseCase.GetUserListResponse::class.java)
                val actualUserDto = actual.data.first()

                actualUserDto.id shouldBe existUser.id
                actualUserDto.nickname shouldBe existUser.nickname
                actualUserDto.profilePath shouldBe existUser.profilePath
            }
            test("getUserList 성공 - id") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val request =
                    GetUserListUseCase.GetUserListRequest(
                        searchNickname = null,
                        nickname = null,
                        id = existUser.id,
                    )
                // when
                val result =
                    mockMvc.get("/users") {
                        param("id", request.id!!)
                        accept = MediaType.APPLICATION_JSON
                    }
                        .andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, GetUserListUseCase.GetUserListResponse::class.java)
                val actualUserDto = actual.data.first()

                actualUserDto.id shouldBe existUser.id
                actualUserDto.nickname shouldBe existUser.nickname
                actualUserDto.profilePath shouldBe existUser.profilePath
            }
            test("getUserList 실패 - 값이 채워진 request param이 2개 이상인 경우") {
                // given
                val status = HttpMessage.HTTP_400.status
                val path = "/users"
                val existUser = repository.save(getUserEntityFixture())
                val request =
                    GetUserListUseCase.GetUserListRequest(
                        searchNickname = existUser.nickname.dropLast(3),
                        nickname = existUser.nickname,
                        id = existUser.id,
                    )
                // when
                val result =
                    mockMvc.get("/users") {
                        param("searchNickname", request.searchNickname!!)
                        param("nickname", request.nickname!!)
                        param("id", request.id!!)
                        accept = MediaType.APPLICATION_JSON
                    }
                        .andExpect { MockMvcResultMatchers.status().isBadRequest }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ErrorData::class.java).data

                actual.status shouldBe status.value()
                actual.error shouldBe status.reasonPhrase
                actual.path shouldBe path
            }
        }

        context("validateUser Test") {
            test("validateUser 성공 ") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val request =
                    ValidateUserUesCase.ValidateUserRequest(
                        existUser.id,
                        existUser.identityVerificationQuestion,
                        existUser.identityVerificationAnswer,
                    )

                // when
                val validateUserRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/identity-verification")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validateUserRequestJson),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ValidateUserUesCase.ValidateUserResponse::class.java)

                val actualUserId = JWTUtil.extractUserIdByToken(actual.data.passwordResetToken)
                actualUserId shouldBe existUser.id
            }

            test("validateUser 실패 - ID, 질문, 대답 Data가 존재하는 것과 한개라도 다름 ") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val request =
                    ValidateUserUesCase.ValidateUserRequest(
                        existUser.id,
                        existUser.identityVerificationQuestion,
                        "NotMatchingAnswer",
                    )

                // when, then
                val validateUserRequestJson = objectMapper.writeValueAsString(request)
                mockMvc.perform(
                    MockMvcRequestBuilders.post("/users/identity-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validateUserRequestJson),
                ).andExpect(MockMvcResultMatchers.status().isNotFound)
                    .andReturn()
                    .response.contentAsString
            }
        }

        context("resetPasswordUser Test") {
            test("resetPasswordUser 성공") {
                // given
                val existUser = repository.save(getUserEntityFixture()).let { userMapper.toDomain(it) }
                val expectedPassword = "resetPassword"
                val request =
                    ResetPasswordUserUseCase.ResetPasswordUserRequestBody(
                        expectedPassword,
                        expectedPassword,
                    )
                val resetPasswordToken = JWTUtil.generatePasswordResetToken(existUser)

                // when
                val resetPasswordRequestBodyJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/${existUser.id}/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $resetPasswordToken")
                            .content(resetPasswordRequestBodyJson),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual =
                    objectMapper.readValue(result, ResetPasswordUserUseCase.ResetPasswordUserResponse::class.java)

                actual.data.id shouldBe existUser.id
                val actualUser = repository.findById(existUser.id).get().let { userMapper.toDomain(it) }
                actualUser.isCorrectPassword(expectedPassword) shouldBe true
            }

            test("resetPasswordUser 실패 - RefreshToken이 없는 경우") {
                // given
                val existUser = repository.save(getUserEntityFixture()).let { userMapper.toDomain(it) }
                val expectedStatus = HttpMessage.HTTP_401.status
                val expectedPath = "/users/${existUser.id}/reset-password"

                val expectedPassword = "resetPassword"
                val request =
                    ResetPasswordUserUseCase.ResetPasswordUserRequestBody(
                        expectedPassword,
                        expectedPassword,
                    )

                // when
                val resetPasswordRequestBodyJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/${existUser.id}/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(resetPasswordRequestBodyJson),
                    ).andExpect(MockMvcResultMatchers.status().isUnauthorized)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ErrorData::class.java).data
                actual.status shouldBe expectedStatus.value()
                actual.error shouldBe expectedStatus.reasonPhrase
                actual.path shouldBe expectedPath
            }

            test("resetPasswordUser 실패 - RefreshToken이 아닌 다른 토큰이 온 경우") {
                // given
                val existUser = repository.save(getUserEntityFixture()).let { userMapper.toDomain(it) }
                val expectedStatus = HttpMessage.HTTP_403.status
                val expectedPath = "/users/${existUser.id}/reset-password"
                val accessToken = JWTUtil.generateAccessToken(existUser)
                val expectedPassword = "resetPassword"
                val request =
                    ResetPasswordUserUseCase.ResetPasswordUserRequestBody(
                        expectedPassword,
                        expectedPassword,
                    )

                // when
                val resetPasswordRequestBodyJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/${existUser.id}/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken")
                            .content(resetPasswordRequestBodyJson),
                    ).andExpect(MockMvcResultMatchers.status().isForbidden)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ErrorData::class.java).data
                actual.status shouldBe expectedStatus.value()
                actual.error shouldBe expectedStatus.reasonPhrase
                actual.path shouldBe expectedPath
            }
        }

        context("createQuotationAlarmTimes Test") {
            test("createQuotationAlarmTimes 성공 ") {
                // given
                val existUser = repository.save(getUserEntityFixture())
                val accessToken = JWTUtil.generateAccessToken(userMapper.toDomain(existUser))
                val localTime = LocalTime.now()
                val request =
                    CreateQuotationAlarmTimeUseCase.CreateQuotationAlarmTimeRequest(
                        quotationAlarmHour = localTime.hour,
                        quotationAlarmMinute = localTime.minute,
                    )

                // when
                val validateUserRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/${existUser.id}/quotation-alarm-time")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken")
                            .content(validateUserRequestJson),
                    ).andExpect(MockMvcResultMatchers.status().isCreated)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual =
                    objectMapper.readValue(
                        result,
                        CreateQuotationAlarmTimeUseCase.CreateQuotationAlarmTimeResponse::class.java,
                    )
                actual.data.id shouldBe existUser.id
            }
            test("createQuotationAlarmTimes 실패 - 중복된 Alarm 설정 ") {
                // given
                val existUser = repository.save(getUserEntityFixtureIncludeQuotationAlarmTimes())
                val accessToken = JWTUtil.generateAccessToken(userMapper.toDomain(existUser))
                val expectedStatus = HttpMessage.HTTP_400.status
                val expectedPath = "/users/${existUser.id}/quotation-alarm-time"
                val localDateTime = existUser.quotationAlarmTimes.first().toLocalDateTime()
                val localTime = localDateTime.toLocalTime()

                val request =
                    CreateQuotationAlarmTimeUseCase.CreateQuotationAlarmTimeRequest(
                        quotationAlarmHour = localTime.hour,
                        quotationAlarmMinute = localTime.minute,
                    )

                // when
                val validateUserRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/${existUser.id}/quotation-alarm-time")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken")
                            .content(validateUserRequestJson),
                    ).andExpect(MockMvcResultMatchers.status().isBadRequest)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ErrorData::class.java).data

                actual.status shouldBe expectedStatus.value()
                actual.error shouldBe expectedStatus.reasonPhrase
                actual.path shouldBe expectedPath
            }
        }

        context("getQuotationAlarmTimes Test") {
            test("getQuotationAlarmTimes 성공 ") {
                // given
                val existUser = repository.save(getUserEntityFixtureIncludeQuotationAlarmTimes())
                val accessToken = JWTUtil.generateAccessToken(userMapper.toDomain(existUser))

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/${existUser.id}/quotation-alarm-time")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken"),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual =
                    objectMapper.readValue(result, GetQuotationAlarmTimeUseCase.GetQuotationAlarmTimeResponse::class.java)
                actual.data.quotationAlarmTimes.first() shouldBe existUser.quotationAlarmTimes.first()
            }
        }

        context("deleteQuotationAlarmTimes Test") {
            test("deleteQuotationAlarmTimes 성공 ") {
                // given
                val existUser = repository.save(getUserEntityFixtureIncludeQuotationAlarmTimes())
                val accessToken = JWTUtil.generateAccessToken(userMapper.toDomain(existUser))

                val timestamp = existUser.quotationAlarmTimes.first()
                val requestLocalTime = timestamp.toLocalDateTime().toLocalTime()

                val request =
                    PatchQuotationAlarmTimeUseCase.PatchQuotationAlarmTimeRequest(
                        quotationAlarmHour = requestLocalTime.hour,
                        quotationAlarmMinute = requestLocalTime.minute,
                    )
                val validateUserRequestJson = objectMapper.writeValueAsString(request)

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/${existUser.id}/quotation-alarm-time")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken")
                            .content(validateUserRequestJson),
                    ).andExpect(MockMvcResultMatchers.status().isCreated)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual =
                    objectMapper.readValue(
                        result,
                        PatchQuotationAlarmTimeUseCase.PatchQuotationAlarmTimeResponse::class.java,
                    )
                actual.data.id shouldBe existUser.id
            }
            test("deleteQuotationAlarmTimes 실패 - 존재하지 않는 시간 삭제 ") {
                // given
                val existUser = repository.save(getUserEntityFixtureIncludeQuotationAlarmTimes())
                val accessToken = JWTUtil.generateAccessToken(userMapper.toDomain(existUser))
                val expectedStatus = HttpMessage.HTTP_404.status
                val expectedPath = "/users/${existUser.id}/quotation-alarm-time"
                val existTime = existUser.quotationAlarmTimes.first()
                val updatedTime = Timestamp.valueOf(existTime.toLocalDateTime().plusHours(1))
                val requestLocalTime = updatedTime.toLocalDateTime().toLocalTime()

                val request =
                    PatchQuotationAlarmTimeUseCase.PatchQuotationAlarmTimeRequest(
                        quotationAlarmHour = requestLocalTime.hour,
                        quotationAlarmMinute = requestLocalTime.minute,
                    )
                val validateUserRequestJson = objectMapper.writeValueAsString(request)

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/${existUser.id}/quotation-alarm-time")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer $accessToken")
                            .content(validateUserRequestJson),
                    ).andExpect(MockMvcResultMatchers.status().isNotFound)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ErrorData::class.java).data

                actual.status shouldBe expectedStatus.value()
                actual.error shouldBe expectedStatus.reasonPhrase
                actual.path shouldBe expectedPath
            }
        }
    }) {
    @MockBean
    lateinit var createProfileImagePort: CreateProfileImagePort

    @MockBean
    lateinit var getProfileImagePort: GetProfileImagePort

    @MockBean
    lateinit var deleteProfileImagePort: DeleteProfileImagePort
}
