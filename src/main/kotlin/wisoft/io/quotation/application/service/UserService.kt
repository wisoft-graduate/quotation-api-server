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
import wisoft.io.quotation.exception.error.InvalidRequestParameterException
import wisoft.io.quotation.exception.error.UserDuplicateException
import wisoft.io.quotation.exception.error.UserNotFoundException
import wisoft.io.quotation.util.JWTUtil
import wisoft.io.quotation.util.SaltUtil
import java.time.Instant

@Service
@Transactional(readOnly = true)
class UserService(
    val jwtUtil: JWTUtil,
    val saltUtil: SaltUtil,
    val saveUserPort: SaveUserPort,
    val getUserByIdPort: GetUserByIdPort,
    val getUserListByNicknamePort: GetUserListByNicknamePort,
    val getUserListByIdPort: GetUserListByIdPort
) : CreateUserUseCase, SignInUseCase,
    DeleteUserUseCase, GetUserListUseCase {
    val logger = KotlinLogging.logger {}

    @Transactional
    override fun createUser(request: CreateUserUseCase.CreateUserRequest): String {
        return runCatching {
            val getUserById = getUserListByIdPort.getUserListById(request.id)
            val getUserByNickname = getUserListByNicknamePort.getUserListByNickname(request.nickname);
            if (getUserById.isNotEmpty() || getUserByNickname.isNotEmpty()) {
                throw UserDuplicateException("id: ${request.id}, nickname: ${request.nickname}")
            }

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
            val user = getUserByIdPort.getByIdOrNull(request.id) ?: throw UserNotFoundException(request.id)

            if (!user.isCorrectPassword(request.password)) {
                throw UserNotFoundException(request.id)
            }

            if (user.isDeleted()) {
                throw UserNotFoundException(request.id)
            }

            SignInUseCase.UserTokenDto(jwtUtil.generateAccessToken(user), jwtUtil.generateRefreshToken(user))
        }.onFailure {
            logger.error { "signIn fail : param[$request]" }
        }.getOrThrow()

    }

    @Transactional
    override fun deleteUser(id: String): String {
        return runCatching {
            val user = getUserByIdPort.getByIdOrNull(id) ?: throw UserNotFoundException(id)

            if (user.isDeleted()) {
                throw UserNotFoundException(id)
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
            val userList: List<User> = when {
                request.id?.isNotEmpty() == true && request.nickname?.isNotEmpty() == false -> {
                    getUserListByIdPort.getUserListById(request.id)
                }

                request.id?.isNotEmpty() == false && request.nickname?.isNotEmpty() == true -> {
                    getUserListByNicknamePort.getUserListByNickname(request.nickname)
                }
                else -> {
                    throw InvalidRequestParameterException(request.toString())
                }
            }
            userList.map { GetUserListUseCase.UserDto(it.id, it.nickname) }
        }.onFailure {
            logger.error { "getUserList fail: param[${request}]" }
        }.getOrThrow()
    }
}