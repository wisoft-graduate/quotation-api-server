package wisoft.io.quotation.adaptor.out.dispatcher

import org.springframework.stereotype.Component
import wisoft.io.quotation.application.port.out.push.PushAlarmNotificationPort
import wisoft.io.quotation.application.port.out.push.PushTagNotificationPort
import java.sql.Timestamp

@Component
class PushAdapter(
    val pushDispatcher: PushDispatcher,
) : PushTagNotificationPort, PushAlarmNotificationPort {
    override fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean {
        return pushDispatcher.sendTagPushNotification(sendUser, tagUser, tagUserId)
    }

    override fun sendAlarmPushNotification(
        userNickname: String,
        userId: String,
        pushTime: Timestamp,
    ): Boolean {
        return pushDispatcher.sendAlarmPushNotification(userNickname, userId, pushTime)
    }
}
