package wisoft.io.quotation.application.port.`in`.comment

import java.util.UUID

interface UpdateCommentUseCase {
    fun updateComment(
        id: UUID,
        request: UpdateCommentRequest,
    ): UUID

    data class UpdateCommentRequest(
        val content: String?,
        val commentedUserId: String?,
    )

    data class UpdateCommentResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
