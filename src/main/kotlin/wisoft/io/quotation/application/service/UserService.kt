package wisoft.io.quotation.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.GetExistUserUseCase
import wisoft.io.quotation.application.port.`in`.DeleteUserUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.CreateUseCase
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.util.JWTUtil

@Service
@Transactional(readOnly = true)
class UserService(
    val jwtUtil: JWTUtil,
    val saveUserPort: SaveUserPort,
    val getUserByIdPort: GetUserByIdPort,
    val getLeaveUserListCountPort: GetLeaveUserListCountPort,
    val getExistUserPort: GetExistUserPort,
    val getExistUserByNicknamePort: GetExistUserByNicknamePort
) : CreateUseCase, SignInUseCase,
    DeleteUserUseCase, GetExistUserUseCase {

    @Transactional
    override fun createUser(request: CreateUseCase.CreateUserRequest): String {
        val existUserById = getExistUserPort.getExistUser(request.id)
        val existUserByNickname = getExistUserByNicknamePort.getExistUserByNickname((request.nickname))
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
        val user = getUserByIdPort.getByIdOrNull(request.id) ?: throw RuntimeException()

        if (!user.isCorrectPassword(request.password)) {
            throw RuntimeException()
        }

        if (!user.isEnrolled()) {
            throw RuntimeException()
        }

        return SignInUseCase.UserTokenDto(jwtUtil.generateAccessToken(user), jwtUtil.generateRefreshToken(user))
    }

    @Transactional
    override fun deleteUser(id: String): String {
        val user = getUserByIdPort.getByIdOrNull(id) ?: throw RuntimeException()
        val leaveCount = getLeaveUserListCountPort.getLeaveUsersCount()

        if (!user.isEnrolled()) {
            throw RuntimeException()
        }

        user.resign(leaveCount + 1)

        return saveUserPort.save(user)
    }

    override fun getExistUser(id: String, nickname: String): Boolean {
        if (id.isNotEmpty()) {
            return getExistUserPort.getExistUser(id)
        }
        if (nickname.isNotEmpty()) {
            return getExistUserByNicknamePort.getExistUserByNickname(nickname)
        }
        return false
    }
}