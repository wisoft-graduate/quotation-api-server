package wisoft.io.quotation.application.port.`in`.quotation

import wisoft.io.quotation.adaptor.out.persistence.entity.view.QuotationRankView
import java.util.UUID

interface GetQuotationRankUseCase {
    fun getQuotationRank(ids: List<UUID>?): List<QuotationRankView>

    data class GetQuotationRankResponse(
        val data: Data,
    )

    data class Data(
        val quotationRanks: List<QuotationRankView>,
    )
}
