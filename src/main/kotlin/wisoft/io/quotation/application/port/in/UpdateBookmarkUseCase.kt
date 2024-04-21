package wisoft.io.quotation.application.port.`in`

import java.util.UUID

interface UpdateBookmarkUseCase {
    fun updateBookmark(
        id: UUID,
        request: UpdateBookmarkRequest,
    ): UUID

    data class UpdateBookmarkRequest(
        val name: String?,
        val quotationIds: List<UUID>?,
        val visibility: Boolean?,
        val icon: String?,
    )

    data class UpdateBookmarkResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
