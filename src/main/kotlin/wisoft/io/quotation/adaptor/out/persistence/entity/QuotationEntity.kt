package wisoft.io.quotation.adaptor.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.time.LocalDateTime
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

@Table(name = "quotation")
@Entity
data class QuotationEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val authorId: UUID,
    val content: String,
    val likeCount: Long,
    val shareCount: Long,
    val commentCount: Long,
    val backgroundImagePath: String,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
)
