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
    val getUserByIdPort: GetUserByIdPort,
    val getUserByNicknamePort: GetUserByNicknamePort,
    val getBookmarkCountByUserIdPort: GetBookmarkCountByUserIdPort,
    val getLikeCountByUserIdPort: GetLikeCountByUserIdPort,
) : CreateUserUseCase, SignInUseCase,
    DeleteUserUseCase, GetUserUseCase, GetUserDetailUseCase {
    val logger = KotlinLogging.logger {}

    @Transactional
    override fun createUser(request: CreateUserUseCase.CreateUserRequest): String {
        return runCatching {
            getUserByIdPort.getByIdOrNull(request.id)?.let {
                throw UserDuplicateException("id: ${request.id}")
            }
            getUserByNicknamePort.getByNicknameOrNull((request.nickname))?.let {
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

            SignInUseCase.UserTokenDto(JWTUtil.generateAccessToken(user), JWTUtil.generateRefreshToken(user))
        }.onFailure {
            logger.error { "signIn fail: param[$request]" }
        }.getOrThrow()

    }

    @Transactional
    override fun deleteUser(id: String): String {
        return runCatching {
            val user = getUserByIdPort.getByIdOrNull(id) ?: throw UserNotFoundException(id)

            if (user.isDeleted()) {
                throw UserNotFoundException(id)
            }

            val identifier = Instant.now().epochSecond.toString() + SaltUtil.generateSalt(4)
            user.resign(identifier)

            createUserPort.create(user)
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
                    getUserByIdPort.getByIdOrNull(this)
                } ?: throw UserNotFoundException("id: ${request.id}")
            } else {
                request.nickname?.run {
                    getUserByNicknamePort.getByNicknameOrNull(this)
                } ?: throw UserNotFoundException("nickname: ${request.nickname}")
            }
            GetUserUseCase.UserDto(user.id, user.nickname)
        }.onFailure {
            logger.error { "getUserList fail: param[${request}]" }
        }.getOrThrow()
    }

    override fun getUserDetailById(request: GetUserDetailUseCase.GetUserDetailByIdRequest): GetUserDetailUseCase.UserDetailDto {
        return runCatching {
            val user = getUserByIdPort.getByIdOrNull(request.id) ?: throw UserNotFoundException("id: ${request.id}")
            val bookmarkCount = getBookmarkCountByUserIdPort.getBookmarkCountByUserId(request.id)
            val likeCount = getLikeCountByUserIdPort.getLikeCountByUserId(request.id)
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


}