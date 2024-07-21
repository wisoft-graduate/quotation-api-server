package wisoft.io.quotation.adaptor.out.dispatcher

import java.sql.Timestamp

interface PushDispatcher {
    fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean

    fun sendAlarmPushNotification(
        userNickname: String,
        userId: String,
        pushTime: Timestamp,
    ): Boolean
}
