package wisoft.io.quotation.application.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import wisoft.io.quotation.application.port.`in`.user.*
import wisoft.io.quotation.application.port.out.*
import wisoft.io.quotation.application.port.out.bookmark.GetBookmarkListPort
import wisoft.io.quotation.application.port.out.s3.CreateProfileImagePort
import wisoft.io.quotation.application.port.out.s3.DeleteProfileImagePort
import wisoft.io.quotation.application.port.out.s3.GetProfileImagePort
import wisoft.io.quotation.application.port.out.user.*
import wisoft.io.quotation.domain.User
import wisoft.io.quotation.exception.error.*
import wisoft.io.quotation.util.JWTUtil
import wisoft.io.quotation.util.SaltUtil
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
    val createProfileImagePort: CreateProfileImagePort,
    val getProfileImagePort: GetProfileImagePort,
    val deleteProfileImagePort: DeleteProfileImagePort,
) : CreateUserUseCase,
    SignInUseCase,
    RefreshTokenUserUseCase,
    DeleteUserUseCase,
    GetUserMyPageUseCase,
    GetUserPageUseCase,
    UpdateUserUseCase,
    GetUserListUseCase,
    ValidateUserUesCase,
    ResetPasswordUserUseCase,
    CreateQuotationAlarmTimeUseCase,
    PatchQuotationAlarmTimeUseCase,
    GetQuotationAlarmTimeUseCase,
    GetProfileImageUseCase {
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

            var profilePath: String? = null
            request.profileImageBase64?.let {
                profilePath = createProfileImagePort.createProfileImage(it)
            }

            val user =
                request.run {
                    User(
                        id = this.id,
                        nickname = this.nickname,
                        identityVerificationQuestion = this.identityVerificationQuestion,
                        identityVerificationAnswer = this.identityVerificationAnswer,
                        profilePath = profilePath,
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

    override fun refreshToken(userId: String): RefreshTokenUserUseCase.UserTokenDto {
        return runCatching {
            val user = getUserPort.getUserById(userId) ?: throw UserNotFoundException(userId)

            RefreshTokenUserUseCase.UserTokenDto(JWTUtil.generateAccessToken(user), JWTUtil.generateRefreshToken(user))
        }.onFailure {
            logger.error { "refreshToken fail: param[id: $userId]" }
        }.getOrThrow()
    }

    @Transactional
    override fun deleteUser(id: String) {
        return runCatching {
            val user = getUserPort.getUserById(id) ?: throw UserNotFoundException(id)

            if (user.isDeleted()) {
                throw UserNotFoundException(id)
            }

            user.profilePath?.let { deleteProfileImagePort.deleteProfileImage(it) }

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

            var profilePath: String? = null
            request.profileImageBase64?.let {
                user.profilePath?.let { deleteProfileImagePort.deleteProfileImage(it) }
                if (!it.equals("null")) {
                    profilePath = createProfileImagePort.createProfileImage(it)
                }
            }
            val updatedUser = user.update(request, profilePath)
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

    override fun getProfileImage(
        userId: String,
        request: GetProfileImageUseCase.GetProfileImageRequest,
    ): String {
        return runCatching {
            getUserPort.getUserById(userId) ?: throw UserNotFoundException("id: $userId")

            getProfileImagePort.getProfileImage(request.id)
        }.onFailure {
            logger.error { "createProfileImage fail: param[userId: $userId, request:$request]" }
        }.getOrThrow()
    }

    @Transactional
    override fun createQuotationAlarmTime(
        userId: String,
        request: CreateQuotationAlarmTimeUseCase.CreateQuotationAlarmTimeRequest,
    ): String {
        return runCatching {
            val user = getUserPort.getUserById(userId) ?: throw UserNotFoundException("id: $userId")

            val localTime = LocalTime.of(request.quotationAlarmHour, request.quotationAlarmMinute)
            val newAlarmTime = LocalDateTime.of(LocalDate.now(), localTime)
            val updatedTimes = user.quotationAlarmTimes.toMutableList()

            val existingTime =
                updatedTimes.find {
                    val existLocalDateTime = it.toLocalDateTime()
                    existLocalDateTime.hour == newAlarmTime.hour && existLocalDateTime.minute == newAlarmTime.minute
                }

            if (existingTime != null) {
                throw QuotationAlarmTimeDuplicateException("hour: ${request.quotationAlarmHour}, minute: ${request.quotationAlarmMinute}")
            } else {
                updatedTimes.add(Timestamp.valueOf(newAlarmTime))
            }

            // 사용자 정보 업데이트
            val updatedUser = user.updateQuotationAlarmTimes(updatedTimes.toList())
            updateUserPort.updateUser(updatedUser)

            user.id
        }.onFailure {
            logger.error { "createQuotationAlarmTime fail: param[userId: $userId, request:$request]" }
        }.getOrThrow()
    }

    @Transactional
    override fun patchQuotationAlarmTime(
        userId: String,
        request: PatchQuotationAlarmTimeUseCase.PatchQuotationAlarmTimeRequest,
    ): String {
        return runCatching {
            val user = getUserPort.getUserById(userId) ?: throw UserNotFoundException("id: $userId")

            val localTime = LocalTime.of(request.quotationAlarmHour, request.quotationAlarmMinute)
            val newAlarmTime = LocalDateTime.of(LocalDate.now(), localTime)
            val updatedTimes = user.quotationAlarmTimes.toMutableList()

            val existingTime =
                updatedTimes.find {
                    val existLocalDateTime = it.toLocalDateTime()
                    existLocalDateTime.hour == newAlarmTime.hour && existLocalDateTime.minute == newAlarmTime.minute
                }

            if (existingTime != null) {
                updatedTimes.remove(existingTime)
            } else {
                throw QuotationAlarmTimeNotFoundException("hour: ${request.quotationAlarmHour}, minute: ${request.quotationAlarmMinute}")
            }

            // 사용자 정보 업데이트
            val updatedUser = user.updateQuotationAlarmTimes(updatedTimes.toList())
            updateUserPort.updateUser(updatedUser)

            user.id
        }.onFailure {
            logger.error { "createQuotationAlarmTime fail: param[userId: $userId, request:$request]" }
        }.getOrThrow()
    }

    override fun getQuotationAlarmTime(userId: String): List<Timestamp> {
        return runCatching {
            val user = getUserPort.getUserById(userId) ?: throw UserNotFoundException("id: $userId")
            user.quotationAlarmTimes
        }.onFailure {
            logger.error { "getQuotationAlarmTime fail: param[userId: $userId]" }
        }.getOrThrow()
    }
}
