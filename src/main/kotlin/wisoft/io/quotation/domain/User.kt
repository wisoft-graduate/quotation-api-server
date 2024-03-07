package wisoft.io.quotation.domain

import jakarta.persistence.Id
import org.mindrot.jbcrypt.BCrypt
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
    @Id
    val id: String,
    var password: String = "",
    var nickname: String,
    val profilePath: String? = null,
    val favoriteQuotation: String? = null,
    val favoriteAuthor: String? = null,
    val commentAlarm: Boolean = false,
    val quotationAlarm: Boolean = false,
    val quotationAlarmTimes: List<Timestamp> = emptyList(),
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
    val identityVerificationQuestion: String,
    val identityVerificationAnswer: String
) {
    fun encryptPassword(password: String) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun isCorrectPassword(inputPassword: String): Boolean {
        return BCrypt.checkpw(inputPassword, this.password)
    }

    fun isEnrolled(): Boolean {
        return !this.nickname.startsWith("leaved#")
    }

    fun resign(count: Number) {
        this.nickname = "leaved#$count"
    }
}
