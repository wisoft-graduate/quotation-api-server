package wisoft.io.quotation.domain

import jakarta.persistence.Id
import jakarta.persistence.Table
import java.sql.Timestamp

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
 */
data class User(
    @Id
    val id: String,
    val password: String,
    val nickname: String,
    val profilePath: String?,
    val favoriteQuotation: String?,
    val favoriteAuthor: String?,
    val likeQuotationCount: Long,
    val bookmarkCount: Long,
    val commentAlarm: Boolean,
    val quotationAlarm: Boolean,
    val quotationAlarmTimes: List<Timestamp> = emptyList(),
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
)
