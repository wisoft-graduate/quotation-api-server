package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank
import java.util.UUID

interface CreateCommentUseCase {
    fun createComment(request: CreateCommentRequest): UUID

    data class CreateCommentRequest(
        val quotationId: UUID,
        @field:NotBlank
        val userId: String,
        @field:NotBlank
        val content: String,
        val commentedUserId: String? = null,
        val parentCommentId: UUID? = null,
    )

    data class CreateCommentResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
