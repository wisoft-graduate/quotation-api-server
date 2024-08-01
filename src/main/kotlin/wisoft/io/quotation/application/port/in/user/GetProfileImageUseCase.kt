package wisoft.io.quotation.application.port.`in`.user

interface GetProfileImageUseCase {
    fun getProfileImage(
        userId: String,
        request: GetProfileImageRequest,
    ): String

    data class GetProfileImageRequest(
        val id: String,
    )

    data class GetProfileImageResponse(
        val data: Data,
    )

    data class Data(
        val base64Image: String,
    )
}
