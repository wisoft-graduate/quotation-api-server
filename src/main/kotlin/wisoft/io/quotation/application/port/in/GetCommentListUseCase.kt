package wisoft.io.quotation.application.port.`in`

import wisoft.io.quotation.domain.Comment
import java.util.UUID

interface GetCommentListUseCase {
    fun getCommentList(request: GetCommentListRequest): List<Comment>

    data class GetCommentListRequest(
        val commentIds: List<UUID>? = null,
        val quotationId: UUID? = null,
        val parentCommentId: UUID? = null,
    )

    data class GetCommentListResponse(
        val data: Data,
    )

    data class Data(
        val commentList: List<Comment>,
    )
}
