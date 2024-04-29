package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.async.methods.BasicHttpRequests.post
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Testcontainers
import wisoft.io.quotation.DatabaseContainerConfig
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.CreateCommentUseCase
import wisoft.io.quotation.fixture.entity.getQuotationEntityFixture
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class CommentControllerTest(
    val mockMvc: MockMvc,
    val quotationRepository: QuotationRepository,
    val userRepository: UserRepository,
    val commentRepository: CommentRepository,
) : FunSpec({

        val objectMapper = ObjectMapper().registerKotlinModule()

        afterEach {
            quotationRepository.deleteAll()
            userRepository.deleteAll()
            commentRepository.deleteAll()
        }

        context("createComment Test") {
            test("createComment 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val commentedUser = userRepository.save(getUserEntityFixture("commentedUser", "nickname2"))
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val request =
                    CreateCommentUseCase.CreateCommentRequest(
                        quotationId = quotation.id,
                        userId = user.id,
                        content = "content",
                        commentedUserId = commentedUser.id,
                    )

                // when
                val createCommentRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createCommentRequestJson),
                    )
                        .andExpect(MockMvcResultMatchers.status().isCreated)
                        .andReturn()
                        .response.contentAsString
                val comment = objectMapper.readValue(result, CreateCommentUseCase.CreateCommentResponse::class.java)

                // then
                val actual = commentRepository.findById(comment.data.id).get()
                actual.quotationId shouldBe request.quotationId
                actual.userId shouldBe request.userId
                actual.content shouldBe request.content
                actual.commentedUserId shouldBe request.commentedUserId
            }

            test("createComment 실패 - 등록되지 않은 사용자") {
                // given
                val commentedUser = userRepository.save(getUserEntityFixture("commentedUser"))
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val request =
                    CreateCommentUseCase.CreateCommentRequest(
                        quotationId = quotation.id,
                        userId = "test",
                        content = "content",
                        commentedUserId = commentedUser.id,
                    )

                // when, then
                val createCommentRequestJson = objectMapper.writeValueAsString(request)
                mockMvc.perform(
                    MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCommentRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }

            test("createComment 실패 - 등록되지 않은 명언") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val commentedUser = userRepository.save(getUserEntityFixture("commentedUser"))
                val request =
                    CreateCommentUseCase.CreateCommentRequest(
                        quotationId = UUID.randomUUID(),
                        userId = user.id,
                        content = "content",
                        commentedUserId = commentedUser.id,
                    )

                // when, then
                val createCommentRequestJson = objectMapper.writeValueAsString(request)
                mockMvc.perform(
                    MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCommentRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }

            test("createComment 실패 - 태그된 유저 등록되지 않음") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val request =
                    CreateCommentUseCase.CreateCommentRequest(
                        quotationId = quotation.id,
                        userId = user.id,
                        content = "content",
                        commentedUserId = "test",
                    )

                // when, then
                val createCommentRequestJson = objectMapper.writeValueAsString(request)
                mockMvc.perform(
                    MockMvcRequestBuilders.post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCommentRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }
    })
