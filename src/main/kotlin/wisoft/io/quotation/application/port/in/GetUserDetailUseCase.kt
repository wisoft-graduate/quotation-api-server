package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank

interface GetUserDetailUseCase {
    fun getUserDetailById(request: GetUserDetailByIdRequest): UserDetailDto

    data class GetUserDetailByIdRequest(
        @field:NotBlank
        val id: String,
    )

    data class GetUserDetailByIdResponse(
        val data: UserDetailDto,
    )

    data class UserDetailDto(
        val id: String,
        val nickname: String,
        val profile: String? = null,
        val favoriteQuotation: String? = null,
        val favoriteAuthor: String? = null,
        val commentAlarm: Boolean,
        val quotationAlarm: Boolean,
        val bookmarkCount: Long,
        val likeQuotationCount: Long,
    )
}
