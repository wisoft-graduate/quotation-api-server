package wisoft.io.quotation.integration.http.adaptor.`in`

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.core.spec.style.FunSpec
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
import wisoft.io.quotation.adaptor.out.persistence.repository.NotificationRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.QuotationRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.comment.CreateCommentUseCase
import wisoft.io.quotation.application.port.`in`.notification.GetNotificationListUseCase
import wisoft.io.quotation.application.port.`in`.notification.UpdateNotificationUseCase
import wisoft.io.quotation.application.service.CommentService
import wisoft.io.quotation.fixture.entity.getNotificationEntityFixture
import wisoft.io.quotation.fixture.entity.getQuotationEntityFixture
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class NotificationControllerTest(
    val mockMvc: MockMvc,
    val userRepository: UserRepository,
    val quotationRepository: QuotationRepository,
    val commentService: CommentService,
    val commentRepository: CommentRepository,
    val notificationRepository: NotificationRepository,
) : FunSpec({

        val objectMapper = ObjectMapper().registerKotlinModule()

        afterEach {
            quotationRepository.deleteAll()
            userRepository.deleteAll()
            commentRepository.deleteAll()
            notificationRepository.deleteAll()
        }

        context("getNotificationList Test") {
            test("getNotificationList 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val commentedUser = userRepository.save(getUserEntityFixture("commentedUser"))
                val quotation = quotationRepository.save(getQuotationEntityFixture(UUID.randomUUID()))
                val commentId =
                    commentService.createComment(
                        CreateCommentUseCase.CreateCommentRequest(
                            quotation.id,
                            user.id,
                            "content",
                            commentedUser.id,
                        ),
                    )

                // when
                val result =
                    mockMvc.perform(MockMvcRequestBuilders.get("/notifications?userId=${user.id}"))
                        .andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString
                val actual =
                    objectMapper.readValue(
                        result,
                        GetNotificationListUseCase.GetNotificationListResponse::class.java,
                    ).data.notificationList.first()

                // then
                actual.commenterId shouldBe user.id
                actual.commentedUserId shouldBe commentedUser.id
                actual.commentId shouldBe commentId
                actual.alarmCheck shouldBe false
            }

            test("getNotificationList 실패") {
                // given
                val userId = "test"

                // when, then
                mockMvc.perform(MockMvcRequestBuilders.get("/notifications?userId=$userId"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }

        context("updateNotification Test") {
            test("updateNotification 성공") {
                // given
                val notification = notificationRepository.save(getNotificationEntityFixture())
                val request = UpdateNotificationUseCase.UpdateNotificationRequest(alarmCheck = true)
                val requestJson = objectMapper.writeValueAsString(request)

                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.put("/notifications/${notification.id}")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON),
                )
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()

                val updatedNotification = notificationRepository.findByIdOrNull(notification.id)!!

                // then
                updatedNotification.id shouldBe notification.id
                updatedNotification.alarmCheck shouldBe true
            }

            test("updateNotification 실패") {
                // given
                val id = UUID.randomUUID()
                val request = UpdateNotificationUseCase.UpdateNotificationRequest(alarmCheck = true)
                val requestJson = objectMapper.writeValueAsString(request)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.put("/notifications/$id")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }

        context("deleteNotification Test") {
            test("deleteNotification 성공") {
                // given
                val notification = notificationRepository.save(getNotificationEntityFixture())

                // when, then
                mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/${notification.id}"))
                    .andExpect(MockMvcResultMatchers.status().isNoContent)
            }

            test("deleteNotification 실패") {
                val id = UUID.randomUUID()

                // when, then
                mockMvc.perform(MockMvcRequestBuilders.delete("/notifications/$id"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }
    })
