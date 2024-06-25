package wisoft.io.quotation.application.port.out.push

interface PushTagNotificationPort {
    fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean
}
