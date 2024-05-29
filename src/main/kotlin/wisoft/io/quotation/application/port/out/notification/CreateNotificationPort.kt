package wisoft.io.quotation.application.port.out.notification

import wisoft.io.quotation.domain.Notification
import java.util.*

interface CreateNotificationPort {
    fun createNotification(notification: Notification): UUID
}
