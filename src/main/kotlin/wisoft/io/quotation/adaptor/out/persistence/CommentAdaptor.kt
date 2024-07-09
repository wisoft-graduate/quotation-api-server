package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.mapeer.CommentMapper
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentRepository
import wisoft.io.quotation.application.port.out.comment.*
import wisoft.io.quotation.domain.Comment
import java.util.UUID

@Component
class CommentAdaptor(
    val commentRepository: CommentRepository,
    val commentCustomRepository: CommentCustomRepository,
    val commentMapper: CommentMapper,
) : CreateCommentPort,
    GetCommentListPort,
    GetCommentPort,
    UpdateCommentPort,
    DeleteCommentPort {
    override fun createComment(comment: Comment): UUID {
        return commentRepository.save(commentMapper.toEntity(comment)).id
    }

    override fun update(comment: Comment): UUID {
        return commentRepository.save(commentMapper.toEntity(comment)).id
    }

    override fun getCommentById(id: UUID): Comment? {
        return commentRepository.findByIdOrNull(id)?.let { commentMapper.toDomain(it) }
    }

    override fun deleteComment(id: UUID) {
        return commentRepository.deleteById(id)
    }

    override fun getCommentList(
        commentIds: List<UUID>?,
        quotationId: UUID?,
        parentId: UUID?,
        isTopDepth: Boolean,
    ): List<Comment> {
        return commentCustomRepository.findCommentList(commentIds, quotationId, parentId, isTopDepth)
            .let { commentMapper.toDomains(it) }
    }
}
