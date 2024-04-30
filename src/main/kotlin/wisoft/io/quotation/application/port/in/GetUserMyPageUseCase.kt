package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank

interface GetUserMyPageUseCase {
    fun getUserMyPage(request: GetUserMyPageRequest): UserMyPageDto

    data class GetUserMyPageRequest(
        @field:NotBlank
        val id: String,
    )

    data class GetUserMyPageResponse(
        val data: UserMyPageDto,
    )

    data class UserMyPageDto(
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
