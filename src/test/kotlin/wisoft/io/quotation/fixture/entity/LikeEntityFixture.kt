package wisoft.io.quotation.fixture.entity

import wisoft.io.quotation.adaptor.out.persistence.entity.LikeEntity
import java.util.*

fun getLikeEntityFixture(quotationId: UUID? = null) =
    LikeEntity(
        id = UUID.randomUUID(),
        userId = "test",
        quotationId = quotationId ?: UUID.randomUUID(),
    )
