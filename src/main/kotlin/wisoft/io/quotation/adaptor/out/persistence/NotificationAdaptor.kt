package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.mapeer.NotificationMapper
import wisoft.io.quotation.adaptor.out.persistence.repository.NotificationRepository
import wisoft.io.quotation.application.port.out.notification.*
import wisoft.io.quotation.domain.Notification
import java.util.*

@Component
class NotificationAdaptor(
    val notificationRepository: NotificationRepository,
    val notificationMapper: NotificationMapper,
) : CreateNotificationPort,
    GetNotificationListPort,
    GetNotificationPort,
    UpdateNotificationPort,
    DeleteNotificationPort {
    override fun getNotification(id: UUID): Notification? {
        return notificationRepository.findByIdOrNull(id)?.let { notificationMapper.toDomain(it) }
    }

    override fun createNotification(notification: Notification): UUID {
        return notificationRepository.save(notificationMapper.toEntity(notification)).id
    }

    override fun getNotificationList(userId: String): List<Notification> {
        return notificationRepository.findByCommenterId(userId).map { notificationMapper.toDomain(it) }
    }

    override fun updateNotification(notification: Notification): UUID {
        return notificationRepository.save(notificationMapper.toEntity(notification)).id
    }

    override fun deleteNotification(id: UUID) {
        return notificationRepository.deleteById(id)
    }
}
