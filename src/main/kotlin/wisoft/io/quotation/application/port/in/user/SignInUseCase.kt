package wisoft.io.quotation.application.port.`in`.user

import jakarta.validation.constraints.NotBlank

interface SignInUseCase {
    fun signIn(request: SignInRequest): UserTokenDto

    data class SignInRequest(
        @field:NotBlank
        val id: String,
        @field:NotBlank
        val password: String,
    )

    data class SignInResponse(
        val data: UserTokenDto,
    )

    data class UserTokenDto(
        val accessToken: String,
        val refreshToken: String,
    )
}
