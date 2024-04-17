package wisoft.io.quotation.domain.dto

class RelatedUserDto {
    data class UpdateUserDto(
        val nickname: String?,
        val profile: String?,
        val favoriteQuotation: String?,
        val favoriteAuthor: String?,
        val quotationAlarm: Boolean?,
        val commentAlarm: Boolean?,
        val identityVerificationQuestion: String?,
        val identityVerificationAnswer: String?,
    )
}