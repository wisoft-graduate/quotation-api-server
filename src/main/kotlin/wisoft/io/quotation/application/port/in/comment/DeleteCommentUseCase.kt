package wisoft.io.quotation.application.port.`in`.comment

import java.util.UUID

interface DeleteCommentUseCase {
    fun deleteComment(id: UUID)
}
