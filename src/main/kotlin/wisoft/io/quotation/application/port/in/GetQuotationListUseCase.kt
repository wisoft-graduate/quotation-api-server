package wisoft.io.quotation.application.port.`in`

import wisoft.io.quotation.domain.Paging
import wisoft.io.quotation.domain.Quotation
import wisoft.io.quotation.domain.QuotationSortTarget
import wisoft.io.quotation.domain.SortDirection
import java.util.UUID

interface GetQuotationListUseCase {
    fun getQuotationList(request: GetQuotationListRequest): List<Quotation>

    data class GetQuotationListRequest(
        val searchWord: String?,
        val sortTarget: QuotationSortTarget?,
        val sortDirection: SortDirection?,
        val paging: Paging?,
        val ids: List<UUID>?,
    )

    data class GetQuotationListResponse(
        val data: Data,
    )

    data class Data(
        val quotations: List<Quotation>,
    )
}
