package wisoft.io.quotation.domain

import jakarta.persistence.Id
import java.sql.Timestamp
import java.util.UUID

/**
 * @property id 식별자
 * @property authorId 명언의 저자 식별자
 * @property content 명언의 내용
 * @property likeCount 명언의 좋아요 수
 * @property shareCount 명언의 공유 수
 * @property commentCount 명언의 댓글 수
 * @property backgroundImagePath 명언의 배경 이미지 경로
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 */
data class Quotation(
    @Id
    val id: UUID = UUID.randomUUID(),
    val content: String,
    val likeCount: Long,
    val shareCount: Long,
    val commentCount: Long,
    val shareLank: Long = 0,
    val likeLank: Long = 0,
    val backgroundImagePath: String,
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
    val author: Author,
)
