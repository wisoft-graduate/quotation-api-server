package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.notification.DeleteNotificationUseCase
import wisoft.io.quotation.application.port.`in`.notification.GetNotificationListUseCase
import wisoft.io.quotation.application.port.`in`.notification.UpdateNotificationUseCase
import wisoft.io.quotation.application.port.out.notification.DeleteNotificationPort
import wisoft.io.quotation.application.port.out.notification.GetNotificationListPort
import wisoft.io.quotation.application.port.out.notification.GetNotificationPort
import wisoft.io.quotation.application.port.out.notification.UpdateNotificationPort
import wisoft.io.quotation.application.port.out.user.GetUserPort
import wisoft.io.quotation.domain.Notification
import wisoft.io.quotation.exception.error.NotificationNotFoundException
import wisoft.io.quotation.exception.error.UserNotFoundException
import java.util.*

@Service
class NotificationService(
    val notificationListPort: GetNotificationListPort,
    val updateNotificationPort: UpdateNotificationPort,
    val getNotificationPort: GetNotificationPort,
    val deleteNotificationPort: DeleteNotificationPort,
    val getUserPort: GetUserPort,
) : GetNotificationListUseCase,
    UpdateNotificationUseCase,
    DeleteNotificationUseCase {
    val logger = KotlinLogging.logger { }

    override fun getNotificationList(userId: String): List<Notification> {
        return runCatching {
            getUserPort.getUserById(userId) ?: throw UserNotFoundException(userId)
            notificationListPort.getNotificationList(userId)
        }.onFailure {
            logger.error { "getNotificationList fail: param[userId: $userId]" }
        }.getOrThrow()
    }

    override fun updateNotification(
        id: UUID,
        request: UpdateNotificationUseCase.UpdateNotificationRequest,
    ): UUID {
        return runCatching {
            val notification =
                getNotificationPort.getNotification(id)
                    ?: throw NotificationNotFoundException(id.toString())
            updateNotificationPort.updateNotification(notification.update(request))
        }.onFailure {
            logger.error { "updateNotification fail: param[$request]" }
        }.getOrThrow()
    }

    override fun deleteNotification(id: UUID) {
        return runCatching {
            getNotificationPort.getNotification(id) ?: throw NotificationNotFoundException(id.toString())
            deleteNotificationPort.deleteNotification(id)
        }.onFailure {
            logger.error { "deleteNotification fail: param[id: $id]" }
        }.getOrThrow()
    }
}
