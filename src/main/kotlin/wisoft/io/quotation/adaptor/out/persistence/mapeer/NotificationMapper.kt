package wisoft.io.quotation.adaptor.out.persistence.mapeer

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.entity.NotificationEntity
import wisoft.io.quotation.domain.Notification

@Component
class NotificationMapper : Mapper<NotificationEntity, Notification> {
    override fun toDomain(entity: NotificationEntity): Notification {
        return Notification(
            id = entity.id,
            commenterId = entity.commenterId,
            commentedUserId = entity.commentedUserId,
            commentId = entity.commentId,
            alarmCheck = entity.alarmCheck,
            createdTime = entity.createdTime,
            lastModifiedTime = entity.lastModifiedTime,
        )
    }

    override fun toEntity(domain: Notification): NotificationEntity {
        return NotificationEntity(
            id = domain.id,
            commenterId = domain.commenterId,
            commentedUserId = domain.commentedUserId,
            commentId = domain.commentId,
            alarmCheck = domain.alarmCheck,
            createdTime = domain.createdTime,
            lastModifiedTime = domain.lastModifiedTime,
        )
    }
}
