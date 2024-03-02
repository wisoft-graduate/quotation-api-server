package wisoft.io.quotation.adaptor.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp
import java.util.UUID

/**
 * @property id 식별자
 * @property commenterId 댓글을 작성한 사용자의 식별자
 * @property commentedUserId 댓글에 태그된 사용자의 식별자
 * @property commentId 댓글 식별자
 * @property alarmCheck 알람 확인
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 */
@Table(name = "notification")
@Entity
data class NotificationEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val commenterId: String,
    val commentedUserId: String,
    val commentId: UUID,
    val alarmCheck: Boolean,
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
)
