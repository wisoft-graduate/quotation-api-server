package wisoft.io.quotation.application.port.out.s3

interface GetProfileImagePort {
    fun getProfileImage(id: String): String
}
