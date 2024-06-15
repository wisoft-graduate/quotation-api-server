package wisoft.io.quotation.adaptor.out.persistence.entity.view

import java.util.*

data class QuotationRankView(
    val id: UUID,
    val likeRank: Long,
    val shareRank: Long,
)
