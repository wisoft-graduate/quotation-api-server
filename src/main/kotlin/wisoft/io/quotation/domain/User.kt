package wisoft.io.quotation.domain

import org.mindrot.jbcrypt.BCrypt
import wisoft.io.quotation.application.port.`in`.user.UpdateUserUseCase
import java.sql.Timestamp
import java.time.LocalDateTime

/**
 * @property id 식별자
 * @property password 비밀번호
 * @property nickname 닉네임
 * @property profilePath 프로필 경로
 * @property favoriteAuthor 가장 좋아하는 저자
 * @property favoriteQuotation 가장 좋아하는 명언
 * @property likeQuotationCount 좋아요를 누른 명언의 수
 * @property bookmarkCount 북마크 수
 * @property commentAlarm 댓글 알람
 * @property quotationAlarm 명언 알람
 * @property quotationAlarmTimes 명언 알람 시간들
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 * @property identityVerificationQuestion 본인 확인 질문
 * @property identityVerificationAnswer 본인 확인 답변
 */
data class User(
    val id: String,
    var password: String = "",
    var nickname: String,
    var profilePath: String? = null,
    var favoriteQuotation: String? = null,
    var favoriteAuthor: String? = null,
    var commentAlarm: Boolean = false,
    var quotationAlarm: Boolean = false,
    var quotationAlarmTimes: List<Timestamp> = emptyList(),
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    var lastModifiedTime: Timestamp? = null,
    var identityVerificationQuestion: String,
    var identityVerificationAnswer: String,
) {
    fun encryptPassword(password: String): User {
        return this.copy(
            password = BCrypt.hashpw(password, BCrypt.gensalt()),
        )
    }

    fun isCorrectPassword(inputPassword: String): Boolean {
        return BCrypt.checkpw(inputPassword, this.password)
    }

    fun isDeleted(): Boolean {
        return this.nickname.startsWith("leaved#")
    }

    fun resign(identifier: String): User {
        return this.copy(
            nickname = "leaved#$identifier",
            profilePath = null,
        )
    }

    fun update(
        dto: UpdateUserUseCase.UpdateUserRequest,
        profilePath: String?,
    ): User {
        return this.copy(
            nickname = dto.nickname ?: this.nickname,
            profilePath = profilePath ?: this.profilePath,
            favoriteQuotation = dto.favoriteQuotation ?: this.favoriteQuotation,
            favoriteAuthor = dto.favoriteAuthor ?: this.favoriteAuthor,
            quotationAlarm = dto.quotationAlarm ?: this.quotationAlarm,
            commentAlarm = dto.commentAlarm ?: this.commentAlarm,
            identityVerificationQuestion = dto.identityVerificationQuestion ?: this.identityVerificationQuestion,
            identityVerificationAnswer = dto.identityVerificationAnswer ?: this.identityVerificationAnswer,
            lastModifiedTime = Timestamp.valueOf(LocalDateTime.now()),
        )
    }

    fun updateProfilePath(profilePath: String?): User {
        return this.copy(
            profilePath = profilePath,
        )
    }

    fun updateQuotationAlarmTimes(updatedQuotationAlarmTimes: List<Timestamp>): User {
        return this.copy(
            quotationAlarmTimes = updatedQuotationAlarmTimes,
        )
    }
}
