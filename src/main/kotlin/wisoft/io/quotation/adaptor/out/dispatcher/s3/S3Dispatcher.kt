package wisoft.io.quotation.adaptor.out.dispatcher.s3

interface S3Dispatcher {
    fun createProfileImage(base64Image: String): String

    fun getProfileImage(id: String): String

    fun deleteProfileImage(id: String)
}
