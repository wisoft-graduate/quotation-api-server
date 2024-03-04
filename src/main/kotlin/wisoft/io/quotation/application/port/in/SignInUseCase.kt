package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank

interface SignInUseCase {

    fun signIn(request: SignInRequest): UserTokenDto

    // Request DTO
    data class SignInRequest(
        @field:NotBlank
        val id: String,
        @field:NotBlank
        val password: String,
    )

    // Response DTO
    data class SignInResponse(
        val data: UserTokenDto
    )

    // Service DTO
    data class UserTokenDto(
        val accessToken: String,
        val refreshToken: String
    )
}