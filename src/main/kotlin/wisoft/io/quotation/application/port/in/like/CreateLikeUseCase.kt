package wisoft.io.quotation.application.port.`in`.like

import java.util.UUID

interface CreateLikeUseCase {
    fun createLike(request: CreateLikeRequest): UUID

    data class CreateLikeRequest(
        val userId: String,
        val quotationId: UUID,
    )

    data class CreateLikeResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
