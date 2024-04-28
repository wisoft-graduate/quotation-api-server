package wisoft.io.quotation.adaptor.out.persistence.entity

import jakarta.persistence.*
import java.sql.Timestamp
import java.util.UUID

/**
 * @property id 식별자
 * @property quotationId 댓글을 작성한 명언의 식별자
 * @property userId 댓글을 작성한 사용자의 식별
 * @property content 댓글 내용
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 * @property parentId 댓글의 상위 댓글 식별자
 */
@Table(name = "comment")
@Entity
data class CommentEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val quotationId: UUID,
    val userId: String,
    val content: String,
    val commentedUserId: String? = null,
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
    val parentId: UUID? = null,
)
