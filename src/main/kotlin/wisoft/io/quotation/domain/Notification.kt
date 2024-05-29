package wisoft.io.quotation.domain

import jakarta.persistence.Id
import wisoft.io.quotation.adaptor.out.persistence.entity.NotificationEntity
import java.sql.Timestamp
import java.time.LocalDateTime
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
data class Notification(
    @Id
    val id: UUID = UUID.randomUUID(),
    val commenterId: String,
    val commentedUserId: String,
    val commentId: UUID,
    val alarmCheck: Boolean,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
) {
    fun toEntity(): NotificationEntity {
        return NotificationEntity(
            id = id,
            commenterId = commenterId,
            commentedUserId = commentedUserId,
            commentId = commentId,
            alarmCheck = alarmCheck,
            createdTime = createdTime,
            lastModifiedTime = lastModifiedTime,
        )
    }

    companion object {
        fun createNotification(
            commenterId: String,
            commentedUserId: String,
            commentId: UUID,
        ): Notification {
            return Notification(
                commenterId = commenterId,
                commentedUserId = commentedUserId,
                commentId = commentId,
                alarmCheck = false,
            )
        }
    }
}
