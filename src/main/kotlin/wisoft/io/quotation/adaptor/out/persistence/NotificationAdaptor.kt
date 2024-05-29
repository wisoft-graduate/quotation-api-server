package wisoft.io.quotation.adaptor.out.persistence

import org.springframework.stereotype.Component
import wisoft.io.quotation.adaptor.out.persistence.repository.NotificationRepository
import wisoft.io.quotation.application.port.out.notification.CreateNotificationPort
import wisoft.io.quotation.domain.Notification
import java.util.*

@Component
class NotificationAdaptor(val notificationRepository: NotificationRepository) : CreateNotificationPort {
    override fun createNotification(notification: Notification): UUID {
        return notificationRepository.save(notification.toEntity()).id
    }
}
