package wisoft.io.quotation.adaptor.out.dispatcher

import org.springframework.stereotype.Component
import wisoft.io.quotation.application.port.out.push.PushTagNotificationPort

@Component
class PushAdapter(
    val pushDispatcher: PushDispatcher,
) : PushTagNotificationPort {
    override fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean {
        return pushDispatcher.sendTagPushNotification(sendUser, tagUser, tagUserId)
    }
}
