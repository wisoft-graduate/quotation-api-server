package wisoft.io.quotation.application.port.`in`

import java.util.UUID

interface GetUserListUseCase {

    fun getUserList(request: GetUserListRequest): List<UserDto>

    data class GetUserListRequest(
        val id: String?,
        val nickname: String?
    )

    data class GetUserListResponse(
        val data: List<UserDto>,
    )

    data class UserDto(
        val id: String,
        val nickname: String,
    )
}