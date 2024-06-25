package wisoft.io.quotation.adaptor.out.dispatcher

interface PushDispatcherImpl {
    fun sendTagPushNotification(
        sendUser: String,
        tagUser: String,
        tagUserId: String,
    ): Boolean
}
