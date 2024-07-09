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
import wisoft.io.quotation.fixture.entity.getAuthorEntityFixture
import wisoft.io.quotation.fixture.entity.getLikeEntityFixture
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
                actual.id shouldBe likeId
                actual.userId shouldBe request.userId
                actual.quotationId shouldBe request.quotationId
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
                val like = likeRepository.save(getLikeEntityFixture())

                // when, then
                mockMvc.delete("/likes/${like.id}").andExpect { HttpStatus.NO_CONTENT }
            }

            test("deleteLike 실패") {
                // given
                val likeId = UUID.randomUUID()

                // when, then
                mockMvc.delete("/likes/$likeId").andExpect { HttpStatus.NOT_FOUND }
            }
        }
    })
