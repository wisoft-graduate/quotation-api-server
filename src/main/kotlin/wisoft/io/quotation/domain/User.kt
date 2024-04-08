package wisoft.io.quotation.domain

import org.mindrot.jbcrypt.BCrypt
import wisoft.io.quotation.adaptor.out.persistence.entity.UserEntity
import wisoft.io.quotation.application.port.`in`.UpdateUserUseCase
import wisoft.io.quotation.domain.dto.RelatedUserDto
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
    var identityVerificationAnswer: String
) {
    fun toEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            password = this.password,
            nickname = this.nickname,
            profilePath = this.profilePath,
            favoriteAuthor = this.favoriteAuthor,
            favoriteQuotation = this.favoriteQuotation,
            commentAlarm = this.commentAlarm,
            quotationAlarm = this.quotationAlarm,
            quotationAlarmTimes = this.quotationAlarmTimes.toTypedArray(),
            createdTime = this.createdTime,
            lastModifiedTime = this.lastModifiedTime,
            identityVerificationQuestion = this.identityVerificationQuestion,
            identityVerificationAnswer = this.identityVerificationAnswer,
        )
    }

    fun encryptPassword(password: String) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun isCorrectPassword(inputPassword: String): Boolean {
        return BCrypt.checkpw(inputPassword, this.password)
    }

    fun isDeleted(): Boolean {
        return this.nickname.startsWith("leaved#")
    }

    fun resign(identifier: String) {
        this.nickname = "leaved#$identifier"
    }

    fun update(dto: RelatedUserDto.UpdateUserDto) {
        dto.nickname?.let { this.nickname = it }
        dto.profile?.let { this.profilePath = it }
        dto.favoriteAuthor?.let { this.favoriteAuthor = it }
        dto.favoriteQuotation?.let { this.favoriteQuotation = it }
        dto.alarm?.let { this.quotationAlarm = it }
    }


}
