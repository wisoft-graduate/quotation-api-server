package wisoft.io.quotation.application.service

import org.springframework.stereotype.Service
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.SignUpUseCase
import wisoft.io.quotation.application.port.out.FindUserPort
import wisoft.io.quotation.application.port.out.SaveUserPort
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.util.JWTUtil

@Service
class UserService(val saveUserPort: SaveUserPort, val findUserPort: FindUserPort) : SignUpUseCase, SignInUseCase {

    override fun signUp(request: SignUpUseCase.SignUpRequest): String {
        val user = request.run {
            User(
                id = this.id,
                nickname = this.nickname,
                identityVerificationQuestion = this.identityVerificationQuestion,
                identityVerificationAnswer = this.identityVerificationAnswer
            )
        }

        user.encryptPassword(request.password)
        return saveUserPort.save(user)
    }

    override fun signIn(request: SignInUseCase.SignInRequest): SignInUseCase.UserTokenDto {
        val user = findUserPort.findByIdOrNull(request.id)

        if(!user.isCorrectPassword(request.password)) {
            throw RuntimeException()
        }

        return SignInUseCase.UserTokenDto(JWTUtil.generateAccessToken(user), JWTUtil.generateRefreshToken(user))
    }
}