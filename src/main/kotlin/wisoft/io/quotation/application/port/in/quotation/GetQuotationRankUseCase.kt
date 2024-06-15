package wisoft.io.quotation.application.port.`in`.quotation

import wisoft.io.quotation.adaptor.out.persistence.entity.view.QuotationRankView
import wisoft.io.quotation.domain.Paging
import wisoft.io.quotation.domain.RankProperty
import java.util.UUID

interface GetQuotationRankUseCase {
    fun getQuotationRank(request: GetQuotationRankRequest): List<QuotationRankView>

    data class GetQuotationRankRequest(
        val ids: List<UUID>?,
        val rankProperty: RankProperty,
        val paging: Paging?,
    )

    data class GetQuotationRankResponse(
        val data: Data,
    )

    data class Data(
        val quotationRanks: List<QuotationRankView>,
    )
}
