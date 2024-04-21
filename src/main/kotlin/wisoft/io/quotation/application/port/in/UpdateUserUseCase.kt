package wisoft.io.quotation.application.port.`in`

interface UpdateUserUseCase {
    fun updateUser(
        id: String,
        request: UpdateUserRequest,
    ): String

    data class UpdateUserRequest(
        val nickname: String?,
        val profilePath: String?,
        val favoriteQuotation: String?,
        val favoriteAuthor: String?,
        val quotationAlarm: Boolean?,
        val commentAlarm: Boolean?,
        val identityVerificationQuestion: String?,
        val identityVerificationAnswer: String?,
    )

    data class UpdateUserResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
