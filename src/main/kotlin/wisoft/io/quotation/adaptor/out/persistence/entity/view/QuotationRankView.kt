package wisoft.io.quotation.adaptor.out.persistence.entity.view

import java.util.*

data class QuotationRankView(
    val id: UUID,
    val rank: Long,
    val count: Long,
    val backgroundImagePath: String,
)
