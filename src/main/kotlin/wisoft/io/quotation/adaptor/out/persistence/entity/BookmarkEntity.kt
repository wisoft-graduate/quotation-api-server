package wisoft.io.quotation.adaptor.out.persistence.entity

import io.hypersistence.utils.hibernate.type.array.UUIDArrayType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.sql.Timestamp
import java.util.UUID

/**
 * @property id 식별자
 * @property name 북마크 이름
 * @property userId 북마크를 생성한 사용자의 식별자
 * @property quotationIds 북마크에 포함된 명언 식별자 목록
 * @property visibility 북마크를 외부 노출
 * @property icon 북마크 icon
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 */
@Table(name = "bookmark")
@Entity
data class BookmarkEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val userId: String,
    @Type(value = UUIDArrayType::class)
    @Column(columnDefinition = "uuid[]")
    val quotationIds: Array<UUID> = emptyArray(),
    val visibility: Boolean,
    val icon: String? = null,
    val createdTime: Timestamp,
    val lastModifiedTime: Timestamp? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookmarkEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (userId != other.userId) return false
        if (!quotationIds.contentEquals(other.quotationIds)) return false
        if (visibility != other.visibility) return false
        if (icon != other.icon) return false
        if (createdTime != other.createdTime) return false
        if (lastModifiedTime != other.lastModifiedTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + quotationIds.contentHashCode()
        result = 31 * result + visibility.hashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + createdTime.hashCode()
        result = 31 * result + (lastModifiedTime?.hashCode() ?: 0)
        return result
    }
}
