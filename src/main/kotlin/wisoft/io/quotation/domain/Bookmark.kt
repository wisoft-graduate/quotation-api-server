package wisoft.io.quotation.domain

import wisoft.io.quotation.adaptor.out.persistence.entity.BookmarkEntity
import wisoft.io.quotation.application.port.`in`.UpdateBookmarkUseCase
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * @property id 식별자
 * @property name 북마크 이름
 * @property userId 북마크를 생성한 사용자의 식별자
 * @property quotationIds 북마크에 포함된 명언 id 목록
 * @property visibility 북마크를 외부 노출
 * @property icon 북마크 icon
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 */
data class Bookmark(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val userId: String,
    val quotationIds: List<UUID> = emptyList(),
    val visibility: Boolean,
    val icon: String? = null,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
) {
    fun toEntity(): BookmarkEntity {
        return BookmarkEntity(
            id = this.id,
            name = this.name,
            userId = this.userId,
            quotationIds = this.quotationIds,
            visibility = this.visibility,
            icon = this.icon,
            createdTime = this.createdTime
        )
    }

    fun update(dto: UpdateBookmarkUseCase.UpdateBookmarkRequest): Bookmark {
        return this.copy(
            name = dto.name ?: this.name,
            quotationIds = dto.quotationIds ?: this.quotationIds,
            visibility = dto.visibility ?: this.visibility,
            icon = dto.icon ?: this.icon,
        )
    }
}
