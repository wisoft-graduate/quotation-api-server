package wisoft.io.quotation.domain

import jakarta.persistence.Id
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

/**
 * @property id 식별자
 * @property userId 좋아요를 누른 사용자의 식별자
 * @property quotationId 좋아요를 누른 명언의 식별자
 * @property createdTime 생성된 시간
 * @property lastModifiedTime 마지막 수정 시간
 */
data class Like(
    @Id
    val id: UUID = UUID.randomUUID(),
    val userId: String,
    val quotationId: UUID,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
)
