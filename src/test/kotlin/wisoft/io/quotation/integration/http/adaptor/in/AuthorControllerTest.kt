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
import wisoft.io.quotation.adaptor.out.persistence.repository.AuthorRepository
import wisoft.io.quotation.application.port.`in`.author.CreateAuthorUseCase
import wisoft.io.quotation.application.port.`in`.author.UpdateAuthorUseCase
import wisoft.io.quotation.exception.error.ErrorData
import wisoft.io.quotation.exception.error.http.HttpMessage
import wisoft.io.quotation.fixture.entity.getAuthorEntityFixture
import java.util.*

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class AuthorControllerTest(
    val mockMvc: MockMvc,
    val repository: AuthorRepository,
) : FunSpec({

        val objectMapper = ObjectMapper().registerKotlinModule()

        afterEach {
            repository.deleteAll()
        }

        context("createAuthor Test") {
            test("createAuthor 성공") {
                // given
                val request =
                    CreateAuthorUseCase.CreateAuthorRequest(
                        name = "testAuthor",
                        countryCode = "082",
                    )
                val requestJson = objectMapper.writeValueAsString(request)

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.post("/authors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson),
                    )
                        .andExpect(MockMvcResultMatchers.status().isCreated)
                        .andReturn()
                        .response.contentAsString

                // then
                val actual = objectMapper.readValue(result, CreateAuthorUseCase.CreateAuthorResponse::class.java)
                val actualAuthor = repository.findById(actual.data.id).get()

                actualAuthor.id shouldBe actual.data.id
                actualAuthor.name shouldBe request.name
                actualAuthor.countryCode shouldBe request.countryCode
            }
        }

        context("updateAuthor Test") {
            test("updateAuthor 성공") {
                // given
                val id = repository.save(getAuthorEntityFixture()).id

                val request =
                    UpdateAuthorUseCase.UpdateAuthorRequest(
                        name = "updatedAuthor",
                        countryCode = "999",
                    )
                val requestJson = objectMapper.writeValueAsString(request)

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.put("/authors/$id")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson),
                    )
                        .andExpect(MockMvcResultMatchers.status().isOk)
                        .andReturn()
                        .response.contentAsString

                val actual = objectMapper.readValue(result, UpdateAuthorUseCase.UpdateAuthorResponse::class.java)
                val actualAuthor = repository.findById(actual.data.id).get()

                // then
                actualAuthor.id shouldBe id
                actualAuthor.name shouldBe request.name
                actualAuthor.countryCode shouldBe request.countryCode
            }

            test("updateAuthor 실패 - id가 존재하지 않는 경우") {
                // given
                val randomId = UUID.randomUUID()
                val status = HttpMessage.HTTP_404.status
                val path = "/authors/$randomId"

                val request =
                    UpdateAuthorUseCase.UpdateAuthorRequest(
                        name = "updatedAuthor",
                        countryCode = "999",
                    )
                val requestJson = objectMapper.writeValueAsString(request)

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.put("/authors/$randomId")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson),
                    )
                        .andExpect(MockMvcResultMatchers.status().isNotFound)
                        .andReturn()
                        .response.contentAsString

                val actual = objectMapper.readValue(result, ErrorData::class.java).data

                // then
                actual.status shouldBe status.value()
                actual.error shouldBe status.reasonPhrase
                actual.path shouldBe path
            }
        }

        context("deleteAuthor Test") {
            test("deleteAuthor 성공") {
                // given
                val id = repository.save(getAuthorEntityFixture()).id

                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.delete("/authors/$id"),
                    )
                        .andExpect(MockMvcResultMatchers.status().isNoContent)

                val existAuthor = repository.findById(id).isEmpty

                // then
                existAuthor shouldBe true
            }

            test("deleteAuthor 실패 - id가 존재하지 않는 경우") {
                // given
                val randomId = UUID.randomUUID()
                val status = HttpMessage.HTTP_404.status
                val path = "/authors/$randomId"
                // when
                val result =
                    mockMvc.perform(
                        MockMvcRequestBuilders.delete("/authors/$randomId"),
                    )
                        .andExpect(MockMvcResultMatchers.status().isNotFound)
                        .andReturn()
                        .response.contentAsString

                val actual = objectMapper.readValue(result, ErrorData::class.java).data

                // then
                actual.status shouldBe status.value()
                actual.error shouldBe status.reasonPhrase
                actual.path shouldBe path
            }
        }
    })
