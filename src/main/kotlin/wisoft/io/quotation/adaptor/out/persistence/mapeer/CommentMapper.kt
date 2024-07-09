package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.CommentEntity
import wisoft.io.quotation.adaptor.out.persistence.repository.CommentRepository
import wisoft.io.quotation.domain.Comment

@Component
class CommentMapper(val commentRepository: CommentRepository) : Mapper<CommentEntity, Comment> {
    fun toDomains(entityList: List<CommentEntity>): List<Comment> {
        val childComments = commentRepository.findByParentIdIn(entityList.map { it.id })
        val childCommentsMap = childComments.groupBy { it.parentId }
        return entityList.map {
            Comment(
                id = it.id,
                quotationId = it.quotationId,
                userId = it.userId,
                content = it.content,
                commentedUserId = it.commentedUserId,
                createdTime = it.createdTime,
                lastModifiedTime = it.lastModifiedTime,
                parentCommentId = it.parentId,
                childCommentIds = childCommentsMap[it.id]?.map { child -> child.id } ?: emptyList(),
            )
        }
    }

    override fun toDomain(entity: CommentEntity): Comment {
        return Comment(
            id = entity.id,
            quotationId = entity.quotationId,
            userId = entity.userId,
            content = entity.content,
            commentedUserId = entity.commentedUserId,
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
            parentCommentId = entity.parentId,
            childCommentIds = commentRepository.findByParentId(entity.id).map { it.id },
        )
    }

    override fun toEntity(domain: Comment): CommentEntity {
        return CommentEntity(
            id = domain.id,
            quotationId = domain.quotationId,
            userId = domain.userId,
            content = domain.content,
            commentedUserId = domain.commentedUserId,
            createdTime = domain.createdTime,
            parentId = domain.parentCommentId,
        )
    }
}
