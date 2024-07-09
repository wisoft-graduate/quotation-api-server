package wisoft.io.quotation.domain

import jakarta.persistence.*
import wisoft.io.quotation.application.port.`in`.comment.UpdateCommentUseCase
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
 * @property childComments 댓글의 하위 댓글 목록
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
    val childComments: List<Comment> = emptyList(),
) {
    fun update(dto: UpdateCommentUseCase.UpdateCommentRequest): Comment {
        return this.copy(
            content = dto.content ?: this.content,
            commentedUserId = dto.commentedUserId ?: this.commentedUserId,
        )
    }
}
