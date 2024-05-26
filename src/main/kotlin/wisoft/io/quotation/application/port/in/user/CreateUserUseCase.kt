package wisoft.io.quotation.application.port.`in`.user

import jakarta.validation.constraints.NotBlank

interface CreateUserUseCase {
    fun createUser(request: CreateUserRequest): String

    data class CreateUserRequest(
        @field:NotBlank
        val id: String,
        @field:NotBlank
        val password: String,
        @field:NotBlank
        val nickname: String,
        @field:NotBlank
        val identityVerificationQuestion: String,
        @field:NotBlank
        val identityVerificationAnswer: String,
    )

    data class CreateUserResponse(
        val data: Data,
    )

    data class Data(
        val id: String,
    )
}
