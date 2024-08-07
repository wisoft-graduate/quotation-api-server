package wisoft.io.quotation.application.port.`in`.user

interface GetUserListUseCase {
    fun getUserList(request: GetUserListRequest): List<UserDto>

    data class GetUserListRequest(
        val nickname: String?,
        val id: String?,
        val searchNickname: String?,
    )

    data class GetUserListResponse(
        val data: List<UserDto>,
    )

    data class UserDto(
        val id: String,
        val nickname: String,
        val profilePath: String? = null,
    )
}
