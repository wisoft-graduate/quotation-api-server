package wisoft.io.quotation.application.port.`in`.quotation

import wisoft.io.quotation.domain.*
import java.util.UUID

interface GetQuotationListUseCase {
    fun getQuotationList(request: GetQuotationListRequest): List<QuotationView>

    data class GetQuotationListRequest(
        val searchWord: String?,
        val sortTarget: QuotationSortTarget?,
        val sortDirection: SortDirection?,
        val rankProperty: RankProperty?,
        val paging: Paging?,
        val ids: List<UUID>?,
    )

    data class GetQuotationListResponse(
        val data: List<QuotationView>,
    )
}
