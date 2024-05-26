package wisoft.io.quotation.application.port.`in`.user

import jakarta.validation.constraints.NotBlank

interface GetUserPageUseCase {
    fun getUserPage(request: GetUserPageRequest): UserPageDto

    data class GetUserPageRequest(
        @field:NotBlank
        val id: String,
    )

    data class GetUserPageResponse(
        val data: UserPageDto,
    )

    data class UserPageDto(
        val id: String,
        val nickname: String,
        val profilePath: String? = null,
        val favoriteQuotation: String? = null,
        val favoriteAuthor: String? = null,
        val bookmarkCount: Long,
        val likeQuotationCount: Long,
    )
}
