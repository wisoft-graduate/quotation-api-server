package wisoft.io.quotation.application.port.out.comment

import java.util.UUID

interface DeleteCommentPort {
    fun deleteComment(id: UUID)
}
