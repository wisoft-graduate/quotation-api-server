package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank

interface ResetPasswordUserUseCase {

    fun resetPasswordUser(request: ResetPasswordUserRequest): String

    data class ResetPasswordUserRequest(
        val password: String,
        val passwordConfirm: String,
        val userId: String,
    )

    data class ResetPasswordUserRequestBody(
        @field:NotBlank
        val password: String,
        @field:NotBlank
        val passwordConfirm: String
    )

    data class ResetPasswordUserResponse(
        val data: Data
    )

    data class Data(
        val id: String
    )

}