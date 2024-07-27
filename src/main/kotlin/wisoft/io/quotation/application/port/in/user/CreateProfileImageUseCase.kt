package wisoft.io.quotation.application.port.`in`.user

interface CreateProfileImageUseCase {
    fun createProfileImage(
        userId: String,
        request: CreateProfileImageRequest,
    ): String

    data class CreateProfileImageRequest(
        val base64Image: String,
    )

    data class CreateProfileImageResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
