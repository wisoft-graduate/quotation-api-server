package wisoft.io.quotation.application.port.out.s3

interface CreateProfileImagePort {
    fun createProfileImage(base64Image: String): String
}
