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
import wisoft.io.quotation.adaptor.out.persistence.repository.UserRepository
import wisoft.io.quotation.application.port.`in`.CreateUserUseCase

@SpringBootTest
@ContextConfiguration(classes = [DatabaseContainerConfig::class])
@Testcontainers
@AutoConfigureMockMvc
class UserControllerTest(val mockMvc: MockMvc, val repository: UserRepository) : FunSpec({

    val objectMapper = ObjectMapper().registerKotlinModule()

    afterEach {
        repository.deleteAll()
    }

    context("signUp Test") {
        test("signUp 성공") {
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
                    .content(signUpRequestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andReturn()
                .response.contentAsString

            // then
            val actual = objectMapper.readValue(result, CreateUserUseCase.CreateUserResponse::class.java)
            actual.data.id shouldBe request.id
        }
    }

})
