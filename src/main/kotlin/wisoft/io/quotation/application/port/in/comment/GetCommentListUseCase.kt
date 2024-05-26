package wisoft.io.quotation.application.port.`in`.comment

import wisoft.io.quotation.domain.Comment
import java.util.UUID

interface GetCommentListUseCase {
    fun getCommentList(request: GetCommentListRequest): List<Comment>

    data class GetCommentListRequest(
        val commentIds: List<UUID>? = null,
        val quotationId: UUID? = null,
        val parentId: UUID? = null,
        val isTopDepth: Boolean = false,
    )

    data class GetCommentListResponse(
        val data: Data,
    )

    data class Data(
        val commentList: List<Comment>,
    )
}
