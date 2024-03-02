package wisoft.io.quotation.application.port.`in`

import jakarta.validation.constraints.NotBlank

interface SignUpUseCase {

    fun signUp(request: SignUpRequest): String
    
    data class SignUpRequest(
        @field:NotBlank
        val id: String,
        @field:NotBlank
        val password: String,
        @field:NotBlank
        val nickname: String,
        @field:NotBlank
        val identityVerificationQuestion: String,
        @field:NotBlank
        val identityVerificationAnswer: String
    )

    data class SignUpResponse(
        val id: String
    )

}