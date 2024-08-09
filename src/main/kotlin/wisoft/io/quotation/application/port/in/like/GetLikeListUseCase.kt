package wisoft.io.quotation.application.port.`in`.like

import wisoft.io.quotation.domain.Like
import java.util.*

interface GetLikeListUseCase {
    fun getLikeList(
        userId: String,
        quotationId: UUID?,
    ): List<Like>

    data class GetLikeListResponse(
        val data: List<Like>,
    )
}
