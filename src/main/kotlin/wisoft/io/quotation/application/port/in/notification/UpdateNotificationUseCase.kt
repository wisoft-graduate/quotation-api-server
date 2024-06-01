package wisoft.io.quotation.application.port.`in`.notification

import java.util.UUID

interface UpdateNotificationUseCase {
    fun updateNotification(
        id: UUID,
        request: UpdateNotificationRequest,
    ): UUID

    data class UpdateNotificationRequest(
        val alarmCheck: Boolean,
    )

    data class UpdateNotificationResponse(
        val data: Data,
    )

    data class Data(
        val id: UUID,
    )
}
