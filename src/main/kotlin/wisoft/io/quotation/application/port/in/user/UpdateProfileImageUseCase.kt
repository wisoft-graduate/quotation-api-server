package wisoft.io.quotation.application.port.`in`.user

interface UpdateProfileImageUseCase {
    fun updateProfileImage(
        userId: String,
        request: UpdateProfileImageRequest,
    ): String

    data class UpdateProfileImageRequest(
        val base64Image: String,
    )

    data class UpdateProfileImageResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
