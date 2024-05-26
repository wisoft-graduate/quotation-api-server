package wisoft.io.quotation.application.port.out.comment

import wisoft.io.quotation.domain.Comment
import java.util.UUID

interface UpdateCommentPort {
    fun update(comment: Comment): UUID
}
