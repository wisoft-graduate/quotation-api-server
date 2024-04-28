package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank
import java.util.UUID

interface CreateBookmarkUseCase {
    fun createBookmark(request: CreateBookmarkRequest): UUID

    data class CreateBookmarkRequest(
        @field:NotBlank
        val userId: String,
        @field:NotBlank
        val name: String,
        val quotationIds: List<UUID> = emptyList(),
        val visibility: Boolean,
        @field:NotBlank
        val icon: String,
    )

    data class CreateBookmarkResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
