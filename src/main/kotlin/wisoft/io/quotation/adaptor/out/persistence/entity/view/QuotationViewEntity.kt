package wisoft.io.quotation.adaptor.out.persistence.entity.view

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

data class QuotationViewEntity(
    val id: UUID = UUID.randomUUID(),
    val authorId: UUID,
    val content: String,
    val commentCount: Long,
    val likeCount: Long,
    val shareCount: Long,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
    val backgroundImagePath: String,
    val rank: Long,
)
