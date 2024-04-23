package wisoft.io.quotation.application.port.`in`

interface GetUserUseCase {
    fun getUserByIdOrNickname(request: GetUserByIdOrNicknameRequest): UserDto

    data class GetUserByIdOrNicknameRequest(
        val id: String?,
        val nickname: String?,
    )

    data class GetUserByIdOrNicknameResponse(
        val data: UserDto,
    )

    data class UserDto(
        val id: String,
        val nickname: String,
    )
}
