package wisoft.io.quotation.application.port.`in`.user

interface DeleteProfileImageUseCase {
    fun deleteProfileImage(
        userId: String,
        request: DeleteProfileImageRequest,
    )

    data class DeleteProfileImageRequest(
        val id: String,
    )
}
