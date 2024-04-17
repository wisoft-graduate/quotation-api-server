package wisoft.io.quotation.application.port.`in`

interface GetUserListUseCase {
    fun getUserList(request: GetUserListRequest): List<UserDto>

    data class GetUserListRequest(
        val likeNickname: String
    )

    data class GetUserListResponse(
        val data: Data
    )

    data class Data(
        val users: List<UserDto>
    )

    data class UserDto(
        val id: String,
        val nickname: String,
        val profilePath: String? = null,
    )
}