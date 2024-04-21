package wisoft.io.quotation.fixture.entity

import wisoft.io.quotation.adaptor.out.persistence.entity.QuotationEntity
import java.util.*

fun getQuotationEntityFixture(authorId: UUID): QuotationEntity {
    return QuotationEntity(
        authorId = authorId,
        content = "content",
        likeCount = 0L,
        shareCount = 0L,
        commentCount = 0L,
        backgroundImagePath = "path",
    )
}
