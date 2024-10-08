package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.testcontainers.junit.jupiter.Testcontainers
import wisoft.io.quotation.DatabaseContainerConfig
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.LikeRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.like.CreateLikeUseCase
import wisoft.io.quotation.application.port.`in`.like.GetLikeListUseCase
import wisoft.io.quotation.fixture.entity.getAuthorEntityFixture
import wisoft.io.quotation.fixture.entity.getQuotationEntityFixture
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class LikeControllerTest(
    val userRepository: UserRepository,
    val quotationRepository: QuotationRepository,
    val likeRepository: LikeRepository,
    val authorRepository: AuthorRepository,
    val mockMvc: MockMvc,
) : FunSpec({

        afterEach {
            likeRepository.deleteAll()
            quotationRepository.deleteAll()
            authorRepository.deleteAll()
            userRepository.deleteAll()
        }

        val objectMapper = ObjectMapper().registerKotlinModule()

        context("createLike Test") {
            test("createLike 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))
                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation.id,
                    )

                // when
                val like =
                    mockMvc.post("/likes") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(request)
                    }.andExpect {
                        status { isCreated() }
                    }.andReturn().response.contentAsString

                val likeId = objectMapper.readValue(like, CreateLikeUseCase.CreateLikeResponse::class.java).data.id

                // then
                val actual = likeRepository.findById(likeId).get()
                val updatedQuotation = quotationRepository.findById(quotation.id).get()
                actual.id shouldBe likeId
                actual.userId shouldBe request.userId
                actual.quotationId shouldBe request.quotationId
                updatedQuotation.likeCount shouldBe 1
            }

            test("createLike 실패 - UserNotFoundException") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = UUID.randomUUID(),
                    )

                // when, then
                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                }
            }

            test("createLike 실패 - QuotationNotFoundException") {
                // given
                val userId = "test"
                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = userId,
                        quotationId = UUID.randomUUID(),
                    )

                // when, then
                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isNotFound() }
                }
            }
        }

        context("deleteLike Test") {
            test("deleteLike 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation.id,
                    )

                val like =
                    mockMvc.post("/likes") {
                        contentType = MediaType.APPLICATION_JSON
                        content = objectMapper.writeValueAsString(request)
                    }.andExpect {
                        status { isCreated() }
                    }.andReturn().response.contentAsString

                val likeId = objectMapper.readValue(like, CreateLikeUseCase.CreateLikeResponse::class.java).data.id

                // when, then
                quotationRepository.findById(quotation.id).get().likeCount shouldBe 1
                mockMvc.delete("/likes/$likeId").andExpect { HttpStatus.NO_CONTENT }
                quotationRepository.findById(quotation.id).get().likeCount shouldBe 0
            }

            test("deleteLike 실패") {
                // given
                val likeId = UUID.randomUUID()

                // when, then
                mockMvc.delete("/likes/$likeId").andExpect { HttpStatus.NOT_FOUND }
            }
        }

        context("getLikeList Test") {
            test("getLikeList 성공 - user, quotation") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation.id,
                    )

                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }

                // when
                val like =
                    mockMvc.get("/likes/${request.userId}") {
                        param("quotationId", "${quotation.id}")
                    }.andExpect {
                        HttpStatus.OK
                    }.andReturn().response.contentAsString

                // then
                val actual = objectMapper.readValue(like, GetLikeListUseCase.GetLikeListResponse::class.java).data.first()
                actual.userId shouldBe user.id
                actual.quotation.id shouldBe quotation.id
            }

            test("getLikeList 성공 - user") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation1 = quotationRepository.save(getQuotationEntityFixture(author.id))
                val quotation2 = quotationRepository.save(getQuotationEntityFixture(author.id))

                val request1 =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation1.id,
                    )

                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request1)
                }

                val request2 =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation2.id,
                    )

                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request2)
                }

                // when
                val like =
                    mockMvc.get("/likes/${user.id}").andExpect {
                        HttpStatus.OK
                    }.andReturn().response.contentAsString

                // then
                val actual = objectMapper.readValue(like, GetLikeListUseCase.GetLikeListResponse::class.java).data
                actual.size shouldBe 2
            }

            test("getLikeList 실패 - Bad Request") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation.id,
                    )

                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }

                // when, then
                mockMvc.get("/likes/${request.userId}") {
                }.andExpect {
                    HttpStatus.BAD_REQUEST
                }
            }

            test("getLikeList 실패 - UserNotFoundException") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation.id,
                    )

                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }

                // when, then
                mockMvc.get("/likes/notExistsUser") {
                    param("quotationId", "${quotation.id}")
                }.andExpect {
                    HttpStatus.NOT_FOUND
                }
            }

            test("getLikeList 실패 - QuotationNotFoundException") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val author = authorRepository.save(getAuthorEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(author.id))

                val request =
                    CreateLikeUseCase.CreateLikeRequest(
                        userId = user.id,
                        quotationId = quotation.id,
                    )

                mockMvc.post("/likes") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }

                // when, then
                mockMvc.get("/likes/${user.id}") {
                    param("quotationId", "${UUID.randomUUID()}")
                }.andExpect {
                    HttpStatus.NOT_FOUND
                }
            }
        }
    })
