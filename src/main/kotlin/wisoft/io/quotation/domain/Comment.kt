package wisoft.io.quotation.domain

import jakarta.persistence.*
import wisoft.io.quotation.adaptor.out.persistence.entity.CommentEntity
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * @property id 식별자
 * @property quotationId 댓글을 작성한 명언의 식별자
 * @property userId 댓글을 작성한 사용자의 식별
 * @property content 댓글 내용
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 * @property parentCommentId 댓글의 상위 댓글 식별자
 * @property childCommentIds 댓글의 하위 댓글 목록
 */
data class Comment(
    @Id
    val id: UUID = UUID.randomUUID(),
    val quotationId: UUID,
    val userId: String,
    val content: String,
    val commentedUserId: String? = null,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
    val parentCommentId: UUID? = null,
    val childCommentIds: List<UUID> = emptyList(),
) {
    fun toEntity(): CommentEntity {
        return CommentEntity(
            id = this.id,
            quotationId = this.quotationId,
            userId = this.userId,
            content = this.content,
            commentedUserId = this.commentedUserId,
            createdTime = this.createdTime,
            parentId = this.parentCommentId,
        )
    }
}
