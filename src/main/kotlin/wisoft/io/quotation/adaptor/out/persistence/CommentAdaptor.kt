package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentCustomRepository
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentRepository
import wisoft.io.quotation.application.port.out.CreateCommentPort
import wisoft.io.quotation.application.port.out.GetCommentListPort
import wisoft.io.quotation.domain.Comment
import java.util.UUID

@Component
class CommentAdaptor(
    val commentRepository: CommentRepository,
    val commentCustomRepository: CommentCustomRepository,
) : CreateCommentPort, GetCommentListPort {
    override fun createComment(comment: Comment): UUID {
        return commentRepository.save(comment.toEntity()).id
    }

    override fun getCommentList(
        commentIds: List<UUID>?,
        quotationId: UUID?,
        parentId: UUID?,
        isTopDepth: Boolean,
    ): List<Comment> {
        // HACK: n+1 문제로 인해 일부 entity에 대해 객체 그래프 형태를 사용할지 고민필요.
        val comments = commentCustomRepository.findCommentList(commentIds, quotationId, parentId, isTopDepth)
        return comments.map {
            val childCommentIds = commentRepository.findByParentId(it.id).map { child -> child.id }
            Comment(
                id = it.id,
                quotationId = it.quotationId,
                userId = it.userId,
                content = it.content,
                commentedUserId = it.commentedUserId,
                createdTime = it.createdTime,
                lastModifiedTime = it.lastModifiedTime,
                childCommentIds = childCommentIds,
            )
        }
    }
}
