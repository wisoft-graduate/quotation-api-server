package wisoft.io.quotation.application.port.out.comment

import wisoft.io.quotation.domain.Comment
import java.util.UUID

interface CreateCommentPort {
    fun createComment(comment: Comment): UUID
}
