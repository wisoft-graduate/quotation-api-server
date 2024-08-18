package wisoft.io.quotation.domain

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

data class QuotationView(
    val id: UUID = UUID.randomUUID(),
    val author: Author,
    val content: String,
    val commentCount: Long,
    val likeCount: Long,
    val shareCount: Long,
    val createdTime: Timestamp = Timestamp.valueOf(LocalDateTime.now()),
    val lastModifiedTime: Timestamp? = null,
    val backgroundImagePath: String,
    val rank: Long,
)
