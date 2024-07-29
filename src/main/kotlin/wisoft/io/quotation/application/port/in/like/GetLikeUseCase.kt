package wisoft.io.quotation.application.port.`in`.like

import wisoft.io.quotation.domain.Like
import java.util.*

interface GetLikeUseCase {
    fun getLike(
        userId: String,
        quotationId: UUID,
    ): Like?

    data class GetLikeResponse(
        val data: Like?,
    )
}
