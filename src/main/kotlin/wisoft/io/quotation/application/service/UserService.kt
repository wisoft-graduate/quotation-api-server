package wisoft.io.quotation.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.ExistUserUseCase
import wisoft.io.quotation.application.port.`in`.ResignUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.SignUpUseCase
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.util.JWTUtil

@Service
@Transactional(readOnly = true)
class UserService(
    val saveUserPort: SaveUserPort,
    val findUserByIdPort: FindUserByIdPort,
    val findLeaveUserListCountPort: FindLeaveUserListCountPort,
    val existUserPort: ExistUserPort,
    val existUserByNicknamePort: ExistUserByNicknamePort
) : SignUpUseCase, SignInUseCase,
    ResignUseCase, ExistUserUseCase {

    @Transactional
    override fun signUp(request: SignUpUseCase.SignUpRequest): String {
        val existUserById = existUserPort.existUser(request.id)
        val existUserByNickname = existUserByNicknamePort.existUserByNickname((request.nickname))
        if (existUserById || existUserByNickname) throw RuntimeException()

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

    @Transactional
    override fun signIn(request: SignInUseCase.SignInRequest): SignInUseCase.UserTokenDto {
        val user = findUserByIdPort.findByIdOrNull(request.id)

        if (!user.isCorrectPassword(request.password)) {
            throw RuntimeException()
        }

        if (!user.isEnrolled()) {
            throw RuntimeException()
        }

        return SignInUseCase.UserTokenDto(JWTUtil.generateAccessToken(user), JWTUtil.generateRefreshToken(user))
    }

    @Transactional
    override fun resign(id: String): String {
        val user = findUserByIdPort.findByIdOrNull(id)
        val leaveCount = findLeaveUserListCountPort.findLeaveUsersCount()

        if (!user.isEnrolled()) {
            throw RuntimeException()
        }

        user.resign(leaveCount + 1)

        return saveUserPort.save(user)
    }

    override fun existUser(id: String, nickname: String): Boolean {
        if (id.isNotEmpty()) {
            return existUserPort.existUser(id)
        }
        if (nickname.isNotEmpty()) {
            return existUserByNicknamePort.existUserByNickname(nickname)
        }
        return false
    }
}