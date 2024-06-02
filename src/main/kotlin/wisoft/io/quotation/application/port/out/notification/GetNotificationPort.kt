package wisoft.io.quotation.application.port.out.notification

import wisoft.io.quotation.domain.Notification
import java.util.UUID

interface GetNotificationPort {
    fun getNotification(id: UUID): Notification?
}
