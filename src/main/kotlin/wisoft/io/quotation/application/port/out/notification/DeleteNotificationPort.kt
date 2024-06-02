package wisoft.io.quotation.application.port.out.notification

import java.util.UUID

interface DeleteNotificationPort {
    fun deleteNotification(id: UUID)
}
