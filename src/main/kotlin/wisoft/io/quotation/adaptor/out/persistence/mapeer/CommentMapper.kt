package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.CommentEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentRepository
import wisoft.io.quotation.domain.Comment

@Component
class CommentMapper(val commentRepository: CommentRepository) : Mapper<CommentEntity, Comment> {
    override fun toEntity(domain: Comment): CommentEntity {
        return CommentEntity(
            id = domain.id,
            quotationId = domain.quotationId,
            userId = domain.userId,
            content = domain.content,
            commentedUserId = domain.commentedUserId,
            createdTime = domain.createdTime,
            parentId = domain.parentCommentId,
            lastModifiedTime = domain.lastModifiedTime,
        )
    }

    fun toDomains(entityList: List<CommentEntity>): List<Comment> {
        val parentIds = entityList.map { it.id }
        val childComments = commentRepository.findByParentIdIn(parentIds)
        val childCommentsMap = childComments.groupBy { it.parentId }

        return entityList.map { it.toDomain(childCommentsMap[it.id] ?: emptyList()) }
    }

    override fun toDomain(entity: CommentEntity): Comment {
        return entity.toDomain(commentRepository.findByParentId(entity.id))
    }

    fun CommentEntity.toDomain(childComments: List<CommentEntity>): Comment {
        return Comment(
            id = this.id,
            quotationId = this.quotationId,
            userId = this.userId,
            content = this.content,
            commentedUserId = this.commentedUserId,
            createdTime = this.createdTime,
            lastModifiedTime = this.lastModifiedTime,
            parentCommentId = this.parentId,
            childComments =
                childComments.map { child ->
                    Comment(
                        id = child.id,
                        quotationId = child.quotationId,
                        userId = child.userId,
                        content = child.content,
                        commentedUserId = child.commentedUserId,
                        createdTime = child.createdTime,
                        lastModifiedTime = child.lastModifiedTime,
                        parentCommentId = child.parentId,
                        // 하위 1depth 까지만 제공하기 위함
                        childComments = emptyList(),
                    )
                },
        )
    }
}
