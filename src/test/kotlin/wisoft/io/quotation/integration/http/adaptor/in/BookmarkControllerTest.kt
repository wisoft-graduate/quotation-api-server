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
import wisoft.io.quotation.adaptor.out.persistence.repository.BookmarkRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.bookmark.CreateBookmarkUseCase
import wisoft.io.quotation.application.port.`in`.bookmark.GetBookmarkListUseCase
import wisoft.io.quotation.application.port.`in`.bookmark.UpdateBookmarkUseCase
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.http.HttpMessage
import wisoft.io.quotation.fixture.entity.getBookmarkEntityFixture
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class BookmarkControllerTest(
    val mockMvc: MockMvc,
    val userRepository: UserRepository,
    val bookmarkRepository: BookmarkRepository,
) : FunSpec({

        val objectMapper = ObjectMapper().registerKotlinModule()

        afterEach {
            bookmarkRepository.deleteAll()
            userRepository.deleteAll()
        }

        context("createBookmark Test") {
            test("createBookmark 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val request =
                    CreateBookmarkUseCase.CreateBookmarkRequest(
                        userId = user.id,
                        quotationIds = listOf(UUID.randomUUID()),
                        name = "name",
                        visibility = false,
                        icon = "icon",
                    )

                // when
                val createBookmarkRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/bookmark")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBookmarkRequestJson),
                    )
                        .andExpect(MockMvcResultMatchers.status().isCreated)
                        .andReturn()
                        .response.contentAsString
                val bookmark = objectMapper.readValue(result, CreateBookmarkUseCase.CreateBookmarkResponse::class.java)

                // then
                val actual = bookmarkRepository.findById(bookmark.data.id).get()
                actual.name shouldBe request.name
                actual.userId shouldBe user.id
                actual.visibility shouldBe request.visibility
                actual.icon shouldBe request.icon
            }

            test("createBookmark 실패 - 등록되지 않은 유저") {
                // given
                val status = HttpMessage.HTTP_404.status
                val userId = "test"
                val path = "/bookmark"
                val request =
                    CreateBookmarkUseCase.CreateBookmarkRequest(
                        userId = userId,
                        quotationIds = listOf(UUID.randomUUID()),
                        name = "name",
                        visibility = false,
                        icon = "icon",
                    )

                // when
                val createBookmarkRequestJson = objectMapper.writeValueAsString(request)
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post(path)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBookmarkRequestJson),
                    )
                        .andExpect(MockMvcResultMatchers.status().isNotFound)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, ErrorData::class.java).data
                actual.status shouldBe status.value()
                actual.error shouldBe status.reasonPhrase
                actual.path shouldBe path
                actual.message shouldBe userId + HttpMessage.HTTP_404.message
            }
        }

        context("getBookmarkList Test") {
            test("getBookmarkList 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val bookmark = bookmarkRepository.save(getBookmarkEntityFixture(user.id))

                // when
                val result =
                    mockMvc.get("/bookmark") {
                        param("userId", user.id)
                        accept = MediaType.APPLICATION_JSON
                    }
                        .andExpect { MockMvcResultMatchers.status().isOk }
                        .andReturn()
                        .response.contentAsString

                // then
                val actual =
                    objectMapper.readValue(
                        result,
                        GetBookmarkListUseCase.GetBookmarkListResponse::class.java,
                    ).data.first()

                actual.id shouldBe bookmark.id
                actual.name shouldBe bookmark.name
                actual.icon shouldBe bookmark.icon
                actual.visibility shouldBe bookmark.visibility
                actual.quotations.map { it.id } shouldBe bookmark.quotationIds
            }
        }

        context("updateBookmark Test") {
            test("updateBookmark 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val bookmark = bookmarkRepository.save(getBookmarkEntityFixture(user.id))
                val request =
                    UpdateBookmarkUseCase.UpdateBookmarkRequest(
                        name = "updated bookmark",
                        quotationIds = listOf(),
                        visibility = false,
                        icon = "updated icon",
                    )

                // when
                val updateBookmarkRequestJson = objectMapper.writeValueAsString(request)
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .put("/bookmark/${bookmark.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBookmarkRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andReturn()
                    .response.contentAsString

                // then
                val actual = bookmarkRepository.findById(bookmark.id).get()
                actual.id shouldBe bookmark.id
                actual.name shouldBe request.name
                actual.quotationIds shouldBe request.quotationIds
                actual.visibility shouldBe request.visibility
                actual.icon shouldBe request.icon
            }

            test("updateBookmark 실패") {
                // given
                val request =
                    UpdateBookmarkUseCase.UpdateBookmarkRequest(
                        name = "updated bookmark",
                        quotationIds = listOf(),
                        visibility = false,
                        icon = "updated icon",
                    )

                // when, // then
                val updateBookmarkRequestJson = objectMapper.writeValueAsString(request)
                mockMvc.perform(
                    MockMvcRequestBuilders
                        .put("/bookmark/${UUID.randomUUID()}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBookmarkRequestJson),
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }

        context("deleteBookmark Test") {
            test("deleteBookmark 성공") {
                // given
                val user = userRepository.save(getUserEntityFixture())
                val bookmark = bookmarkRepository.save(getBookmarkEntityFixture(user.id))

                // when, then
                mockMvc.perform(MockMvcRequestBuilders.delete("/bookmark/${bookmark.id}"))
                    .andExpect(MockMvcResultMatchers.status().isNoContent)
            }

            test("deleteBookmark 실패") {
                // given
                val randomUUID = UUID.randomUUID()

                // when, then
                mockMvc.perform(MockMvcRequestBuilders.delete("/bookmark/$randomUUID"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
            }
        }
    })
