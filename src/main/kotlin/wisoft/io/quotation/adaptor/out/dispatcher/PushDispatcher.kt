package wisoft.io.quotation.adaptor.out.dispatcher

interface PushDispatcher {
    fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean
}
