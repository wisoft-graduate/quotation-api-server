package wisoft.io.quotation.domain

import jakarta.persistence.Id
import wisoft.io.quotation.application.port.`in`.author.UpdateAuthorUseCase
import java.sql.Timestamp
import java.time.LocalDateTime
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
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
) {
    fun update(dto: UpdateAuthorUseCase.UpdateAuthorRequest): Author {
        return this.copy(
            name = dto.name ?: this.name,
            countryCode = dto.countryCode ?: countryCode,
        )
    }
}
