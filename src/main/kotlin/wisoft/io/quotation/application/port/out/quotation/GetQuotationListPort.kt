package wisoft.io.quotation.application.port.out.quotation

import wisoft.io.quotation.application.port.`in`.quotation.GetQuotationListUseCase
import wisoft.io.quotation.domain.Quotation

interface GetQuotationListPort {
    fun getQuotationList(request: GetQuotationListUseCase.GetQuotationListRequest): List<Quotation>
}
