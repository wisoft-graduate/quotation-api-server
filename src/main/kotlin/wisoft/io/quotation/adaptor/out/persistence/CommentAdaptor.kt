package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentRepository
import wisoft.io.quotation.application.port.out.CreateCommentPort
import wisoft.io.quotation.domain.Comment
import java.util.UUID

@Component
class CommentAdaptor(val commentRepository: CommentRepository) : CreateCommentPort {
    override fun createComment(comment: Comment): UUID {
        return commentRepository.save(comment.toEntity()).id
    }
}
