package wisoft.io.quotation.domain

import jakarta.persistence.Table
import java.sql.Timestamp
import java.util.UUID

/**
 * @property id 식별자
 * @property name 북마크 이름
 * @property userId 북마크를 생성한 사용자의 식별자
 * @property quotations 북마크에 포함된 명언 목록
 * @property visibility 북마크를 외부 노출
 * @property icon 북마크 icon
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 */
data class Bookmark(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val userId: String,
    val quotations: List<Quotation> = emptyList(),
    val visibility: Boolean,
    val icon: String? = null,
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
)
