package wisoft.io.quotation.application.port.`in`

interface UpdateUserUseCase {

    fun updateUser(id: String, request: UpdateUserRequest): String

    data class UpdateUserRequest(
        val nickname: String?,
        val profile: String?,
        val alarm: Boolean?,
        val favoriteQuotation: String?,
        val favoriteAuthor: String?,
    )

    data class UpdateUserResponse(
        val data: Data
    )

    data class Data(
        val id: String
    )
}