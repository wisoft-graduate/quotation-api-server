package wisoft.io.quotation.adaptor.out.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import wisoft.io.quotation.domain.User
import java.sql.Timestamp

/**
 * @property id 식별자
 * @property password 비밀번호
 * @property nickname 닉네임
 * @property profilePath 프로필 경로
 * @property favoriteAuthor 가장 좋아하는 저자
 * @property favoriteQuotation 가장 좋아하는 명언
 * @property commentAlarm 댓글 알람
 * @property quotationAlarm 명언 알람
 * @property quotationAlarmTimes 명언 알람 시간들
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 * @property identityVerificationQuestion 본인 확인 질문
 * @property identityVerificationAnswer 본인 확인 답변
 */

@Table(name = "account")
@Entity
data class UserEntity(
    @Id
    val id: String,
    val password: String,
    val nickname: String,
    val profilePath: String? = null,
    val favoriteQuotation: String? = null,
    val favoriteAuthor: String? = null,
    val commentAlarm: Boolean,
    val quotationAlarm: Boolean,
//    val quotationAlarmTimes: List<Timestamp> = emptyList(),
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
    val identityVerificationQuestion: String,
    val identityVerificationAnswer: String
) {
    companion object {
        fun from(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                password = user.password,
                nickname = user.nickname,
                profilePath = user.profilePath,
                favoriteAuthor = user.favoriteAuthor,
                favoriteQuotation = user.favoriteQuotation,
                commentAlarm = user.commentAlarm,
                quotationAlarm = user.quotationAlarm,
//                quotationAlarmTimes = user.quotationAlarmTimes,
                createdTime = user.createdTime,
                lastModifiedTime = user.lastModifiedTime,
                identityVerificationQuestion = user.identityVerificationQuestion,
                identityVerificationAnswer = user.identityVerificationAnswer,
            )
        }
        fun to(userEntity: UserEntity): User {
            return User(
                id = userEntity.id,
                password = userEntity.password,
                nickname = userEntity.nickname,
                profilePath = userEntity.profilePath,
                favoriteAuthor = userEntity.favoriteAuthor,
                favoriteQuotation = userEntity.favoriteQuotation,
                commentAlarm = userEntity.commentAlarm,
                quotationAlarm = userEntity.quotationAlarm,
//                quotationAlarmTimes = userEntity.quotationAlarmTimes,
                createdTime = userEntity.createdTime,
                lastModifiedTime = userEntity.lastModifiedTime,
                identityVerificationQuestion = userEntity.identityVerificationQuestion,
                identityVerificationAnswer = userEntity.identityVerificationAnswer,
            )
        }
    }
}
