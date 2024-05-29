package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
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
import wisoft.io.quotation.application.port.`in`.comment.CreateCommentUseCase
import wisoft.io.quotation.application.port.`in`.comment.GetCommentListUseCase
import wisoft.io.quotation.application.port.`in`.comment.UpdateCommentUseCase
import wisoft.io.quotation.fixture.entity.getCommentEntityFixture
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

        context("deleteComment Test") {
            test("deleteComment 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val comment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id))

                // when, then
                mockMvc.perform(MockMvcRequestBuilders.delete("/comments/${comment.id}"))
                    .andExpect(MockMvcResultMatchers.status().isNoContent)
            }

            test("deleteComment 실패") {
                // given
                val id = UUID.randomUUID()

                // when, then
                mockMvc.perform(MockMvcRequestBuilders.delete("/comments/$id"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }

        context("updateComment Test") {
            test("updateComment 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val commentedUser = userRepository.save(getUserEntityFixture("commentedUser", "nickname2"))
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val comment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id))
                val request =
                    UpdateCommentUseCase.UpdateCommentRequest(
                        content = "updated content",
                        commentedUserId = commentedUser.id,
                    )
                val updateCommentRequestJson = objectMapper.writeValueAsString(request)

                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.put("/comments/${comment.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCommentRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
                val updatedComment = commentRepository.findByIdOrNull(comment.id)

                // then
                updatedComment?.content shouldBe request.content
                updatedComment?.commentedUserId shouldBe request.commentedUserId
            }

            test("updateComment 실패 - 태그된 사용자 존재 x") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val comment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id))
                val request =
                    UpdateCommentUseCase.UpdateCommentRequest(
                        content = "updated content",
                        commentedUserId = "등록되지 않은 사용자 아이디",
                    )
                val updateCommentRequestJson = objectMapper.writeValueAsString(request)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.put("/comments/${comment.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCommentRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
                    .andReturn()
            }

            test("updateComment 실패 - 댓글 존재 x") {
                // given
                val request = UpdateCommentUseCase.UpdateCommentRequest(content = null, commentedUserId = null)
                val updateCommentRequestJson = objectMapper.writeValueAsString(request)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.put("/comments/${UUID.randomUUID()}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCommentRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
                    .andReturn()
            }
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

        context("getCommentList Test") {
            test("getCommentList 성공 - 명언에 대한 댓글 목록 조회시 최상위 댓글 및 하위 1depth 하위 댓글을 조회할 수 있다.") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val comment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id))
                val childComment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id, comment.id))

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("/comments")
                            .param("quotationId", quotation.id.toString())
                            .param("isTopDepth", true.toString())
                            .contentType(MediaType.APPLICATION_JSON),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                val actual =
                    objectMapper.readValue(
                        result,
                        GetCommentListUseCase.GetCommentListResponse::class.java,
                    ).data.commentList.first()

                // then
                actual.id shouldBe comment.id
                actual.content shouldBe comment.content
                actual.userId shouldBe comment.userId
                actual.commentedUserId shouldBe comment.commentedUserId
                actual.quotationId shouldBe comment.quotationId
                actual.childCommentIds shouldContain childComment.id
            }

            test("getCommentList 성공 - commentId 목록으로 댓글을 조회 할 수 있다.") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val comment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id))

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("/comments")
                            .param("commentIds", comment.id.toString())
                            .contentType(MediaType.APPLICATION_JSON),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                val actual =
                    objectMapper.readValue(
                        result,
                        GetCommentListUseCase.GetCommentListResponse::class.java,
                    ).data.commentList.first()

                // then
                actual.id shouldBe comment.id
                actual.content shouldBe comment.content
                actual.userId shouldBe comment.userId
                actual.commentedUserId shouldBe comment.commentedUserId
                actual.quotationId shouldBe comment.quotationId
            }

            test("getCommentList 성공 - 1depth 하위 댓글을 조회 할 수 있다.") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val comment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id))
                val childComment = commentRepository.save(getCommentEntityFixture(quotation.id, user.id, comment.id))

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.get("/comments")
                            .param("parentId", comment.id.toString())
                            .contentType(MediaType.APPLICATION_JSON),
                    ).andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                val actual =
                    objectMapper.readValue(
                        result,
                        GetCommentListUseCase.GetCommentListResponse::class.java,
                    ).data.commentList.first()

                // then
                actual.id shouldBe childComment.id
                actual.content shouldBe childComment.content
                actual.userId shouldBe childComment.userId
                actual.commentedUserId shouldBe childComment.commentedUserId
                actual.quotationId shouldBe childComment.quotationId
            }
        }
    })
