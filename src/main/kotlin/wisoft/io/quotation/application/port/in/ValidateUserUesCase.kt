package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank

interface ValidateUserUesCase {
    fun validateUser(request: ValidateUserRequest): Data

    data class ValidateUserRequest(
        @field:NotBlank
        val id: String,
        @field:NotBlank
        val identityVerificationQuestion: String,
        @field:NotBlank
        val identityVerificationAnswer: String
    )

    data class ValidateUserResponse(
        val data: Data,
    )

    data class Data(
        val passwordResetToken: String
    )
}