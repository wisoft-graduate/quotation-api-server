package wisoft.io.quotation.domain.dto

class RelatedUserDto {
    data class UpdateUserDto(
        val nickname: String?,
        val profile: String?,
        val alarm: Boolean?,
        val favoriteQuotation: String?,
        val favoriteAuthor: String?,
    )
}