package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.NotificationRepository
import wisoft.io.quotation.application.port.out.notification.*
import wisoft.io.quotation.domain.Notification
import java.util.*

@Component
class NotificationAdaptor(
    val notificationRepository: NotificationRepository,
) : CreateNotificationPort,
    GetNotificationListPort,
    GetNotificationPort,
    UpdateNotificationPort,
    DeleteNotificationPort {
    override fun getNotification(id: UUID): Notification? {
        return notificationRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun createNotification(notification: Notification): UUID {
        return notificationRepository.save(notification.toEntity()).id
    }

    override fun getNotificationList(userId: String): List<Notification> {
        return notificationRepository.findByCommenterId(userId).map { it.toDomain() }
    }

    override fun updateNotification(notification: Notification): UUID {
        return notificationRepository.save(notification.toEntity()).id
    }

    override fun deleteNotification(id: UUID) {
        return notificationRepository.deleteById(id)
    }
}
