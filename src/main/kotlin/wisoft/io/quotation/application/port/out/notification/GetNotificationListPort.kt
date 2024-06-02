package wisoft.io.quotation.application.port.out.notification

import wisoft.io.quotation.domain.Notification

interface GetNotificationListPort {
    fun getNotificationList(userId: String): List<Notification>
}
