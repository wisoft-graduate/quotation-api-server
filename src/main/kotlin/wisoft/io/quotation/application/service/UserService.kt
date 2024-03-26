package wisoft.io.quotation.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.GetUserListUseCase
import wisoft.io.quotation.application.port.`in`.DeleteUserUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.CreateUserUseCase
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.util.JWTUtil
import java.util.LinkedList

@Service
@Transactional(readOnly = true)
class UserService(
    val jwtUtil: JWTUtil,
    val saveUserPort: SaveUserPort,
    val getUserByIdPort: GetUserByIdPort,
    val getLeaveUserListCountPort: GetLeaveUserListCountPort,
    val getUserListPort: GetUserListPort,
) : CreateUserUseCase, SignInUseCase,
    DeleteUserUseCase, GetUserListUseCase {

    @Transactional
    override fun createUser(request: CreateUserUseCase.CreateUserRequest): String {
        val ids = LinkedList<String>()
        ids.push(request.id)
        val nicknameList = LinkedList<String>()
        nicknameList.push(request.nickname)

        val idsRequest = GetUserListUseCase.GetUserListRequest(ids = ids, nicknameList = null)
        val nicknameListRequest = GetUserListUseCase.GetUserListRequest(ids = null, nicknameList = nicknameList)

        val getUserById = getUserListPort.getUserList(idsRequest)
        val getUserByNickname = getUserListPort.getUserList(nicknameListRequest)
        if (getUserById.isNotEmpty() || getUserByNickname.isNotEmpty()) throw RuntimeException()

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

    override fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<GetUserListUseCase.UserDto> {
        val userList = getUserListPort.getUserList(request)
        return userList.map {
            GetUserListUseCase.UserDto(it.id, it.nickname)
        }
    }
}