package wisoft.io.quotation.fixture.entity

import wisoft.io.quotation.adaptor.out.persistence.entity.LikeEntity
import java.util.*

fun getLikeEntityFixture() =
    LikeEntity(
        id = UUID.randomUUID(),
        userId = "test",
        quotationId = UUID.randomUUID(),
    )
