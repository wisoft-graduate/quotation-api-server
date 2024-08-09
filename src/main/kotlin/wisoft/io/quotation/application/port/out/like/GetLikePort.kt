package wisoft.io.quotation.application.port.out.like

import wisoft.io.quotation.domain.Like
import java.util.UUID

interface GetLikePort {
    fun getLikeById(id: UUID): Like?

    fun getLikeByUserIdAndQuotationId(
        userId: String,
        quotationId: UUID,
    ): Like?

    fun getLikeByUserId(userId: String): List<Like>
}
