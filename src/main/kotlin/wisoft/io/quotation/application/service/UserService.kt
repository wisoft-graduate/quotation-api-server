package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.GetUserListUseCase
import wisoft.io.quotation.application.port.`in`.DeleteUserUseCase
import wisoft.io.quotation.application.port.`in`.SignInUseCase
import wisoft.io.quotation.application.port.`in`.CreateUserUseCase
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.util.JWTUtil
import wisoft.io.quotation.util.SaltUtil
import java.time.Instant
import java.util.LinkedList

@Service
@Transactional(readOnly = true)
class UserService(
    val jwtUtil: JWTUtil,
    val saltUtil: SaltUtil,
    val saveUserPort: SaveUserPort,
    val getUserByIdPort: GetUserByIdPort,
    val getUserListPort: GetUserListPort,
) : CreateUserUseCase, SignInUseCase,
    DeleteUserUseCase, GetUserListUseCase {
    val logger = KotlinLogging.logger {}

    @Transactional
    override fun createUser(request: CreateUserUseCase.CreateUserRequest): String {
        return runCatching {
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

            saveUserPort.save(user)
        }.onFailure {
            logger.error { "createUser Fail : param[$request]" }
        }.getOrThrow()

    }

    @Transactional
    override fun signIn(request: SignInUseCase.SignInRequest): SignInUseCase.UserTokenDto {
        return runCatching {
            val user = getUserByIdPort.getByIdOrNull(request.id) ?: throw RuntimeException()

            if (!user.isCorrectPassword(request.password)) {
                throw RuntimeException()
            }

            if (!user.isEnrolled()) {
                throw RuntimeException()
            }

            SignInUseCase.UserTokenDto(jwtUtil.generateAccessToken(user), jwtUtil.generateRefreshToken(user))
        }.onFailure {
            logger.error { "signIn fail : param[$request]" }
        }.getOrThrow()

    }

    @Transactional
    override fun deleteUser(id: String): String {
        return runCatching {
            val user = getUserByIdPort.getByIdOrNull(id) ?: throw RuntimeException()

            if (!user.isEnrolled()) {
                throw RuntimeException()
            }

            val identifier = Instant.now().epochSecond.toString() + saltUtil.generateSalt(4)
            user.resign(identifier)

            saveUserPort.save(user)
        }.onFailure {
            logger.error { "deleteUser fail : parma[id: $id]" }
        }.getOrThrow()

    }

    override fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<GetUserListUseCase.UserDto> {
        return runCatching {
            val userList = getUserListPort.getUserList(request)
            userList.map {
                GetUserListUseCase.UserDto(it.id, it.nickname)
            }
        }.onFailure {
            logger.error { "getUserList fail: param[${request}]" }
        }.getOrThrow()
    }
}