package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.*
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.exception.error.InvalidRequestParameterException
import wisoft.io.quotation.exception.error.InvalidUserException
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
    val deleteUserPort: DeleteUserPort,
) : CreateUserUseCase,
    SignInUseCase,
    DeleteUserUseCase,
    GetUserMyPageUseCase,
    GetUserPageUseCase,
    UpdateUserUseCase,
    GetUserListUseCase,
    ValidateUserUesCase,
    ResetPasswordUserUseCase {
    val logger = KotlinLogging.logger {}

    @Transactional
    override fun createUser(request: CreateUserUseCase.CreateUserRequest): String {
        return runCatching {
            getUserPort.getUserById(request.id)?.let {
                throw UserDuplicateException("아이디")
            }
            getUserPort.getUserByNickname((request.nickname))?.let {
                throw UserDuplicateException("닉네임")
            }

            val user =
                request.run {
                    User(
                        id = this.id,
                        nickname = this.nickname,
                        identityVerificationQuestion = this.identityVerificationQuestion,
                        identityVerificationAnswer = this.identityVerificationAnswer,
                    )
                }
            val encryptPasswordUser = user.encryptPassword(request.password)

            createUserPort.create(encryptPasswordUser)
        }.onFailure {
            logger.error { "createUser fail: param[$request]" }
        }.getOrThrow()
    }

    override fun getUserList(request: GetUserListUseCase.GetUserListRequest): List<GetUserListUseCase.UserDto> {
        return runCatching {
            // FIXME: 추후 가능 하면 요청 정보 중 하나만 받도록 Validate 에서 제한
            val valueCount = listOf(request.id, request.nickname, request.searchNickname).count { it != null }
            if (valueCount != 1) throw InvalidRequestParameterException("parameter valueCount: $valueCount")

            val userList = getUserListPort.getUserList(request)
            userList.map {
                GetUserListUseCase.UserDto(
                    id = it.id,
                    nickname = it.nickname,
                    profilePath = it.profilePath,
                )
            }
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

    override fun getUserMyPage(request: GetUserMyPageUseCase.GetUserMyPageRequest): GetUserMyPageUseCase.UserMyPageDto {
        return runCatching {
            val user = getUserPort.getUserById(request.id) ?: throw UserNotFoundException("id: ${request.id}")
            val bookmarkCount = getBookmarkListPort.getBookmarkListCountByUserId(request.id)
            val likeCount = getLikeListPort.getLikeListCountByUserId(request.id)
            GetUserMyPageUseCase.UserMyPageDto(
                user.id,
                user.nickname,
                user.profilePath,
                user.favoriteQuotation,
                user.favoriteAuthor,
                user.commentAlarm,
                user.quotationAlarm,
                bookmarkCount,
                likeCount,
            )
        }.onFailure {
            logger.error { "getUserDetailById fail: param[$request]" }
        }.getOrThrow()
    }

    override fun getUserPage(request: GetUserPageUseCase.GetUserPageRequest): GetUserPageUseCase.UserPageDto {
        return runCatching {
            val user = getUserPort.getUserById(request.id) ?: throw UserNotFoundException("id: ${request.id}")
            val bookmarkCount = getBookmarkListPort.getBookmarkListCountByUserId(request.id)
            val likeCount = getLikeListPort.getLikeListCountByUserId(request.id)
            GetUserPageUseCase.UserPageDto(
                id = user.id,
                nickname = user.nickname,
                profilePath = user.profilePath,
                favoriteQuotation = user.favoriteQuotation,
                favoriteAuthor = user.favoriteAuthor,
                bookmarkCount = bookmarkCount,
                likeQuotationCount = likeCount,
            )
        }.onFailure {
            logger.error { "getUserDetailById fail: param[$request]" }
        }.getOrThrow()
    }

    @Transactional
    override fun updateUser(
        id: String,
        request: UpdateUserUseCase.UpdateUserRequest,
    ): String {
        return runCatching {
            val user = getUserPort.getUserById(id) ?: throw UserNotFoundException("id: $id")
            val updatedUser = user.update(request)
            updateUserPort.updateUser(updatedUser)
        }.onFailure {
            logger.error { "updateUser fail: param[id: $id, request: $request]" }
        }.getOrThrow()
    }

    override fun validateUser(request: ValidateUserUesCase.ValidateUserRequest): ValidateUserUesCase.Data {
        return runCatching {
            val user =
                getUserPort.getUserByIdentityInformation(
                    request.id,
                    request.identityVerificationQuestion,
                    request.identityVerificationAnswer,
                ) ?: throw InvalidUserException("request: $request")

            val passwordResetToken = JWTUtil.generatePasswordResetToken(user)

            ValidateUserUesCase.Data(passwordResetToken = passwordResetToken)
        }.onFailure {
            logger.error { "validateUser fail: param[$request]" }
        }.getOrThrow()
    }

    @Transactional
    override fun resetPasswordUser(request: ResetPasswordUserUseCase.ResetPasswordUserRequest): String {
        return runCatching {
            val user = getUserPort.getUserById(request.userId) ?: throw UserNotFoundException("id: $request.userId")
            val updatedUser = user.encryptPassword(request.password)

            updateUserPort.updateUser(updatedUser)
        }.onFailure {
            logger.error { "resetPasswordUser fail: param[request:$request" }
        }.getOrThrow()
    }
}
