package wisoft.io.quotation.application.port.`in`.notification

import java.util.UUID

interface DeleteNotificationUseCase {
    fun deleteNotification(id: UUID)
}
