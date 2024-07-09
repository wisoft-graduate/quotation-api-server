package wisoft.io.quotation.adaptor.out.persistence.entity

import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.sql.Timestamp
import java.time.LocalDateTime
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
    @Type(value = ListArrayType::class)
    @Column(columnDefinition = "uuid[]")
    val quotationIds: List<UUID> = listOf(),
    val visibility: Boolean,
    val icon: String? = null,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
)
