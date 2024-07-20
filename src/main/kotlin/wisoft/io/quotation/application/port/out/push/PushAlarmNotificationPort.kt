package wisoft.io.quotation.application.port.out.push

import java.sql.Timestamp

interface PushAlarmNotificationPort {
    fun sendAlarmPushNotification(
        userNickname: String,
        userId: String,
        pushTime: Timestamp,
    ): Boolean
}
