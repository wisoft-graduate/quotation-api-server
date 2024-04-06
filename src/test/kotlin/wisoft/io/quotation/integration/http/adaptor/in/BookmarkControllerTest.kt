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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Testcontainers
import wisoft.io.quotation.DatabaseContainerConfig
import wisoft.io.quotation.adaptor.out.persistence.repository.BookmarkRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.CreateBookmarkUseCase
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.http.HttpMessage
import wisoft.io.quotation.fixture.entity.getUserEntityFixture
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class BookmarkControllerTest(
    val mockMvc: MockMvc,
    val userRepository: UserRepository,
    val bookmarkRepository: BookmarkRepository
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
            val request = CreateBookmarkUseCase.CreateBookmarkRequest(
                id = UUID.randomUUID(),
                userId = user.id,
                quotationIds = listOf(UUID.randomUUID()),
                name = "name",
                visibility = false,
                icon = "icon"
            )

            // when
            val createBookmarkRequestJson = objectMapper.writeValueAsString(request)
            val result = mockMvc.perform(MockMvcRequestBuilders.post("/bookmark")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBookmarkRequestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()
                .response.contentAsString


            // then
            val actual = objectMapper.readValue(result, CreateBookmarkUseCase.CreateBookmarkResponse::class.java)
            actual.data.id shouldBe request.id
        }

        test("createBookmark 실패 - 등록되지 않은 유저") {
            // given
            val status = HttpMessage.HTTP_404.status
            val userId = "test"
            val path = "/bookmark"
            val request = CreateBookmarkUseCase.CreateBookmarkRequest(
                userId = userId,
                quotationIds = listOf(UUID.randomUUID()),
                name = "name",
                visibility = false,
                icon = "icon"
            )

            // when
            val createBookmarkRequestJson = objectMapper.writeValueAsString(request)
            val result = mockMvc.perform(MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBookmarkRequestJson))
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

})