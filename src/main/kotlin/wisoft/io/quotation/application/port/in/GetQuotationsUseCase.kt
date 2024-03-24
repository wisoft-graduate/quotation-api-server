package wisoft.io.quotation.application.port.`in`

import wisoft.io.quotation.domain.Paging
import wisoft.io.quotation.domain.Quotation
import wisoft.io.quotation.domain.QuotationSortTarget
import wisoft.io.quotation.domain.SortDirection
import java.util.UUID


interface GetQuotationsUseCase {

    fun getQuotations(request: GetQuotationsRequest): List<Quotation>

    data class GetQuotationsRequest(
        val searchWord: String?,
        val sortTarget: QuotationSortTarget?,
        val sortDirection: SortDirection?,
        val paging: Paging?,
        val ids: List<UUID>?
    )

    data class GetQuotationsResponse(
        val data: Data
    )

    data class Data(
        val quotations: List<Quotation>
    )

}