package wisoft.io.quotation.domain

import jakarta.persistence.Id
import java.sql.Timestamp
import java.util.UUID

/**
 * @property id 식별자
 * @property name 저자의 이름
 * @property countryCode 저자의 국적 코드
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 */
data class Author(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val countryCode: String,
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
)
