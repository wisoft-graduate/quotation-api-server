package wisoft.io.quotation.application.port.`in`.user

interface RefreshTokenUserUseCase {
    fun refreshToken(userId: String): UserTokenDto

    data class RefreshTokenUserResponse(
        val data: UserTokenDto,
    )

    data class UserTokenDto(
        val accessToken: String,
        val refreshToken: String,
    )
}
