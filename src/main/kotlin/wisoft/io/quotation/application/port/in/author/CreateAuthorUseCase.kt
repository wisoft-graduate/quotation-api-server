package wisoft.io.quotation.application.port.`in`.author

import jakarta.validation.constraints.NotBlank
import java.util.*

interface CreateAuthorUseCase {
    fun createAuthor(request: CreateAuthorRequest): UUID

    data class CreateAuthorRequest(
        @field:NotBlank
        val name: String,
        @field:NotBlank
        val countryCode: String,
    )

    data class CreateAuthorResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
