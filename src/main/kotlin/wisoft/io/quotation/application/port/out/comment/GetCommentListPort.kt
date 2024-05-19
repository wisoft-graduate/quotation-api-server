package wisoft.io.quotation.application.port.out.comment

import wisoft.io.quotation.domain.Comment
import java.util.UUID

interface GetCommentListPort {
    fun getCommentList(
        commentIds: List<UUID>?,
        quotationId: UUID?,
        parentId: UUID?,
        isTopDepth: Boolean,
    ): List<Comment>
}
