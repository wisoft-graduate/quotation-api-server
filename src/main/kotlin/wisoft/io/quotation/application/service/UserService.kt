package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.*
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
    val createUserPort: CreateUserPort,
    val getUserPort: GetUserPort,
    val getUserListPort: GetUserListPort,
    val getLikeListPort: GetLikeListPort,
    val getBookmarkListPort: GetBookmarkListPort,
    val updateUserPort: UpdateUserPort,
    val deleteUserPort: DeleteUserPort
) : CreateUserUseCase, SignInUseCase,
    DeleteUserUseCase, GetUserUseCase, GetUserDetailUseCase, UpdateUserUseCase, GetUserListUseCase {
    val logger = KotlinLogging.logger {}

    @Transactional
    override fun createUser(request: CreateUserUseCase.CreateUserRequest): String {
        return runCatching {
            getUserPort.getUserById(request.id)?.let {
                throw UserDuplicateException("id: ${request.id}")
            }
            getUserPort.getUserByNickname((request.nickname))?.let {
                throw UserDuplicateException("nickname: ${request.nickname}")
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

            createUserPort.create(user)
        }.onFailure {
            logger.error { "createUser fail: param[$request]" }
        }.getOrThrow()
    }

    override fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<GetUserListUseCase.UserDto> {
        return runCatching {
            val userList = getUserListPort.getUserList(request.likeNickname)
            userList.map { GetUserListUseCase.UserDto(id = it.id, nickname = it.nickname, profilePath = it.profilePath) }
        }.onFailure {
            logger.error { "getUserList fail: param[$request]" }
        }.getOrThrow()
    }

    override fun signIn(request: SignInUseCase.SignInRequest): SignInUseCase.UserTokenDto {
        return runCatching {
            val user = getUserPort.getUserById(request.id) ?: throw UserNotFoundException(request.id)

            if (!user.isCorrectPassword(request.password)) {
                throw UserNotFoundException(request.id)
            }

            if (user.isDeleted()) {
                throw UserNotFoundException(request.id)
            }

            SignInUseCase.UserTokenDto(JWTUtil.generateAccessToken(user), JWTUtil.generateRefreshToken(user))
        }.onFailure {
            logger.error { "signIn fail: param[$request]" }
        }.getOrThrow()

    }

    @Transactional
    override fun deleteUser(id: String) {
        return runCatching {
            val user = getUserPort.getUserById(id) ?: throw UserNotFoundException(id)

            if (user.isDeleted()) {
                throw UserNotFoundException(id)
            }

            val identifier = Instant.now().epochSecond.toString() + SaltUtil.generateSalt(4)
            val deletedUser = user.resign(identifier)

            deleteUserPort.deleteUser(deletedUser)
        }.onFailure {
            logger.error { "deleteUser fail: parma[id: $id]" }
        }.getOrThrow()

    }

    override fun getUserByIdOrNickname(request: GetUserUseCase.GetUserByIdOrNicknameRequest): GetUserUseCase.UserDto {
        return runCatching {
            if (request.id === null && request.nickname === null) throw InvalidRequestParameterException(request.toString())
            else if (request.id !== null && request.nickname !== null) throw InvalidRequestParameterException(request.toString())

            val user: User = if (request.id !== null) {
                request.id.run {
                    getUserPort.getUserById(this)
                } ?: throw UserNotFoundException("id: ${request.id}")
            } else {
                request.nickname?.run {
                    getUserPort.getUserByNickname(this)
                } ?: throw UserNotFoundException("nickname: ${request.nickname}")
            }
            GetUserUseCase.UserDto(user.id, user.nickname)
        }.onFailure {
            logger.error { "getUserList fail: param[${request}]" }
        }.getOrThrow()
    }

    override fun getUserDetailById(request: GetUserDetailUseCase.GetUserDetailByIdRequest): GetUserDetailUseCase.UserDetailDto {
        return runCatching {
            val user = getUserPort.getUserById(request.id) ?: throw UserNotFoundException("id: ${request.id}")
            val bookmarkCount = getBookmarkListPort.getBookmarkListCountByUserId(request.id)
            val likeCount = getLikeListPort.getLikeListCountByUserId(request.id)
            GetUserDetailUseCase.UserDetailDto(
                user.id,
                user.nickname,
                user.profilePath,
                user.favoriteQuotation,
                user.favoriteAuthor,
                user.commentAlarm,
                user.quotationAlarm,
                bookmarkCount,
                likeCount
            )
        }.onFailure {
            logger.error { "getUserDetailById fail: param[${request}]" }
        }.getOrThrow()
    }

    @Transactional
    override fun updateUser(id: String, request: UpdateUserUseCase.UpdateUserRequest): String {
        return runCatching {
            val user = getUserPort.getUserById(id) ?: throw UserNotFoundException("id: ${id}")
            val updatedUser = user.update(request)
            updateUserPort.updateUser(updatedUser)
        }.onFailure {
            logger.error { "updateUser fail: param[id: ${id}, request: ${request}]" }
        }.getOrThrow()
    }


}