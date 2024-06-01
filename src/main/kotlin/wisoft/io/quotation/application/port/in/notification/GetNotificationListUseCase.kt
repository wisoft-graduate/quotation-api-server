package wisoft.io.quotation.application.port.`in`.notification

import wisoft.io.quotation.domain.Notification

interface GetNotificationListUseCase {
    fun getNotificationList(userId: String): List<Notification>

    data class GetNotificationListResponse(
        val data: Data,
    )

    data class Data(
        val notificationList: List<Notification>,
    )
}
