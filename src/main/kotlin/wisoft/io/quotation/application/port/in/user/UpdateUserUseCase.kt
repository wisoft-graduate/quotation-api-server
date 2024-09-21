package wisoft.io.quotation.application.port.`in`.user

interface UpdateUserUseCase {
    fun updateUser(
        id: String,
        request: UpdateUserRequest,
    ): String

    data class UpdateUserRequest(
        val nickname: String?,
        val favoriteQuotation: String?,
        val favoriteAuthor: String?,
        val quotationAlarm: Boolean?,
        val commentAlarm: Boolean?,
        val identityVerificationQuestion: String?,
        val identityVerificationAnswer: String?,
        val profileImageBase64: String?,
        // 변수 추가
        val isProfileImageDelete: Boolean?
    )

    data class UpdateUserResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
